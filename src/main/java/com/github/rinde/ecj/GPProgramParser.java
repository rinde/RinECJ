package com.github.rinde.ecj;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import ec.gp.GPNode;

/**
 * @author Rinde van Lon (rinde.vanlon@cs.kuleuven.be)
 *
 */
public final class GPProgramParser {

  private static final String SUPER_NAME = "super";
  private static final String SPACE = " ";
  private static final String L_BRACE = "(";
  private static final String R_BRACE = ")";

  private GPProgramParser() {}

  public static String fixBraces(String program) {

    final StringBuilder sb = new StringBuilder();

    boolean addedBrace = false;
    for (int i = 0; i < program.length(); i++) {
      final char cur = program.charAt(i);
      final boolean prevIsLetterOrBrace = i > 0
        && (program.charAt(i - 1) == '('
          || isAllowableFuncChar(program.charAt(i - 1)));
      final boolean isLetter = isAllowableFuncChar(cur);

      if (isLetter && !prevIsLetterOrBrace) {
        sb.append(L_BRACE);
        addedBrace = true;
      } else if (!isLetter && addedBrace) {
        addedBrace = false;
        sb.append(R_BRACE);
      }
      sb.append(cur);
    }
    return sb.toString();
  }

  public static <T> GPProgram<T> parseProgram(String program,
      Collection<? extends GPBaseNode<T>> functions) {
    final Map<String, GPBaseNode<T>> funcMap = newHashMap();
    for (final GPBaseNode<T> func : functions) {
      funcMap.put(func.name(), func);
    }

    final Node n = new Node(SUPER_NAME);
    parseProgram(fixBraces(program), 0, n);
    return convertToGPProgram(convert(n.children.get(0), funcMap));
  }

  public static <T> GPProgram<T> parseProgramFunc(String program,
      Collection<? extends GPFunc<T>> functions) {
    final Map<String, GPBaseNode<T>> funcMap = newHashMap();
    for (final GPFunc<T> func : functions) {
      funcMap.put(func.name(), new GPBaseNode<T>(func));
    }
    final Node n = new Node(SUPER_NAME);
    parseProgram(fixBraces(program), 0, n);
    return convertToGPProgram(convert(n.children.get(0), funcMap));
  }

  @SuppressWarnings("unchecked")
  public static <C> GPProgram<C> convertToGPProgram(GPBaseNode<C> root) {

    final List<GPNode> list = newArrayList();
    final Queue<GPNode> head = newLinkedList();
    head.add(root);

    while (!head.isEmpty()) {
      final GPNode cur = head.poll();
      if (cur.children.length > 0) {
        for (int i = 0; i < cur.children.length; i++) {
          head.add(cur.children[i]);
        }
      }
      list.add(cur);
    }

    Collections.reverse(list);
    final Map<GPBaseNode<C>, GPFuncNode<C>> map = newHashMap();
    for (final GPNode n : list) {
      if (n.children.length == 0) {
        map.put((GPBaseNode<C>) n,
          new GPFuncNode<C>(((GPBaseNode<C>) n).getFunc()));
      } else {
        final GPFuncNode<C>[] arr = new GPFuncNode[n.children.length];
        for (int i = 0; i < arr.length; i++) {
          arr[i] = map.get(n.children[i]);
        }
        map.put((GPBaseNode<C>) n,
          new GPFuncNode<C>(((GPBaseNode<C>) n).getFunc(), arr));
      }
    }
    return new GPProgram<C>(map.get(root));
  }

  public static String toLisp(GPProgram<?> prog) {
    return toLisp(prog.root);
  }

  static final char NODE_PREFIX = 'n';

  public static String toDot(GPProgram<?> prog) {

    final StringBuilder string = new StringBuilder();
    string.append("digraph mapgraph {").append(System.lineSeparator());

    final Map<GPFuncNode<?>, Integer> idMap = new LinkedHashMap<>();
    int nodeId = 0;
    final List<GPFuncNode<?>> todo = new ArrayList<>();
    todo.add(prog.root);

    while (!todo.isEmpty()) {
      final GPFuncNode<?> cur = todo.remove(0);
      string.append(NODE_PREFIX)
        .append(nodeId)
        .append("[label=\"")
        .append(cur.getFunction().name())
        .append("\"]")
        .append(System.lineSeparator());

      idMap.put(cur, new Integer(nodeId));
      nodeId++;

      for (int i = 0; i < cur.getNumChildren(); i++) {
        todo.add(cur.getChild(i));
      }
    }

    todo.add(prog.root);
    while (!todo.isEmpty()) {
      final GPFuncNode<?> cur = todo.remove(0);

      for (int i = 0; i < cur.getNumChildren(); i++) {
        final GPFuncNode<?> child = cur.getChild(i);
        string.append(NODE_PREFIX)
          .append(idMap.get(cur))
          .append(" -> ")
          .append(NODE_PREFIX)
          .append(idMap.get(child))
          .append(System.lineSeparator());

        todo.add(child);
      }
    }

    string.append("}").append(System.lineSeparator());
    return string.toString();

  }

  public static String toLisp(GPFuncNode<?> node) {
    if (node.getNumChildren() == 0) {
      return node.getFunction().name();
    } else {
      final StringBuilder sb = new StringBuilder(L_BRACE);
      sb.append(node.getFunction().name());
      for (int x = 0; x < node.getNumChildren(); x++) {
        sb.append(SPACE).append(toLisp(node.getChild(x)));
      }
      return sb.append(R_BRACE).toString();
    }
  }

  static boolean isAllowableFuncChar(char ch) {
    return Character.isLetter(ch) || Character.isDigit(ch) || ch == '.';
  }

  private static <T> GPBaseNode<T> convert(Node n,
      Map<String, GPBaseNode<T>> funcMap) {
    // final GPNode gpnode = new GPFunc(n.name, n.children.size());
    checkArgument(funcMap.containsKey(n.name),
      "The function with name: \"%s\" is not known to the parser.", n.name);
    final GPBaseNode<T> node = funcMap.get(n.name).create();
    node.children = new GPBaseNode[n.children.size()];
    checkState(n.children.size() == node.getNumChildren(),
      "the supplied program is invalid, the number of children does not match "
        + "the expected number of children");
    for (int i = 0; i < n.children.size(); i++) {
      node.children[i] = convert(n.children.get(i), funcMap);
    }
    return node;
  }

  private static int parseProgram(String string, int index, Node n) {
    boolean funcStart = false;
    final StringBuilder funcName = new StringBuilder();
    Node current = null;
    for (int i = index; i < string.length(); i++) {
      if (string.charAt(i) == '(' && !funcStart) {
        funcStart = true;
      } else if (funcStart) {
        if (current == null
          && (string.charAt(i) == '('
            || string.charAt(i) == ')'
            || string.charAt(i) == ' ')) {
          current = new Node(funcName.toString());
          n.addChild(current);
        }

        if (string.charAt(i) == '(') {
          // new function, will be argument of parent
          i = parseProgram(string, i, current);
        } else if (string.charAt(i) == ')') {
          // end of function definition
          return i;
        } else {
          funcName.append(string.charAt(i));
        }
      }
    }
    return -1;
  }

  static class Node {
    final String name;
    final List<Node> children;

    Node(String nm) {
      name = nm;
      children = new ArrayList<Node>();
    }

    void addChild(Node n) {
      children.add(n);
    }

    @Override
    public String toString() {
      if (children.isEmpty()) {
        return L_BRACE + name + R_BRACE;
      }
      return L_BRACE + name + SPACE + children.toString()
        .replace("[", "")
        .replace("]", "")
        .replace(",", "").trim()
        + R_BRACE;
    }
  }
}
