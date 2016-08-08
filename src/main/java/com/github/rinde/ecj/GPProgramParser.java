package com.github.rinde.ecj;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.HashMultimap;

import ec.gp.GPNode;

/**
 * @author Rinde van Lon (rinde.vanlon@cs.kuleuven.be)
 *
 */
public class GPProgramParser {

  public static void parse() {
    // (foreach (nearbypacks) (addtoplan (foreachpackref)))

    final String input = "(foreach (nearbypacks) (addtoplan (foreachpackref)))";

    // Pattern pat = Pattern.compile("\\(([a-z-\\(\\)\\s]*)\\)$");
    final Pattern pat = Pattern.compile("\\(([a-z-]*)");

    // input.

    // System.out.println(input.matches("\\(([a-z-\\(\\)\\s]*)\\)$"));

    final String newInput = input;
    // while (true) {
    final Matcher m = pat.matcher(newInput);
    if (m.matches()) {

      for (int i = 1; i <= m.groupCount(); i++) {
        System.out.println(m.group(i));
      }
    }

    System.out.println(input);
    final Node n = new Node("super");
    System.out.println(parseProgram(input, 0, n));
    System.out.println(n.children.get(0));
    // }
  }

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
        sb.append("(");
        addedBrace = true;
      } else if (!isLetter && addedBrace) {
        addedBrace = false;
        sb.append(")");
      }
      sb.append(cur);
    }
    return sb.toString();
  }

  static boolean isAllowableFuncChar(char ch) {
    return Character.isLetter(ch) || Character.isDigit(ch) || ch == '.';
  }

  public static <T> GPProgram<T> parseProgram(String program,
      Collection<? extends GPBaseNode<T>> functions) {
    final Map<String, GPBaseNode<T>> funcMap = newHashMap();
    for (final GPBaseNode<T> func : functions) {
      funcMap.put(func.name(), func);
    }

    final Node n = new Node("super");
    parseProgram(fixBraces(program), 0, n);
    return convertToGPProgram(convert(n.children.get(0), funcMap));
  }

  public static <T> GPProgram<T> parseProgramFunc(String program,
      Collection<? extends GPFunc<T>> functions) {
    final Map<String, GPBaseNode<T>> funcMap = newHashMap();
    for (final GPFunc<T> func : functions) {
      funcMap.put(func.name(), new GPBaseNode<T>(func));
    }
    final Node n = new Node("super");
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

  public static String toLisp(GPFuncNode<?> node) {
    if (node.getNumChildren() == 0) {
      return node.getFunction().name();
    } else {
      final StringBuilder sb = new StringBuilder("(");
      sb.append(node.getFunction().name());
      for (int x = 0; x < node.getNumChildren(); x++) {
        sb.append(" ").append(toLisp(node.getChild(x)));
      }
      return sb.append(")").toString();
    }
  }

  private static <T> GPBaseNode<T> convert(Node n,
      Map<String, GPBaseNode<T>> funcMap) {
    // final GPNode gpnode = new GPFunc(n.name, n.children.size());
    checkArgument(funcMap.containsKey(n.name),
      "The function with name: \"%s\" is not known to the parser.", n.name);
    final GPBaseNode<T> node = funcMap.get(n.name).create();
    node.children = new GPBaseNode[n.children.size()];
    checkState(n.children.size() == node.getNumChildren(),
      "the supplied program is invalid, the number of children does not match the expected number of children");
    for (int i = 0; i < n.children.size(); i++) {
      node.children[i] = convert(n.children.get(i), funcMap);
    }
    return node;
  }

  static class Node {

    String name;
    List<Node> children;

    public Node(String name) {
      this.name = name;
      children = new ArrayList<Node>();
    }

    void addChild(Node n) {
      children.add(n);
    }

    @Override
    public String toString() {
      if (children.isEmpty()) {
        return "(" + name + ")";
      }
      return "(" + name + " " + children.toString().replace("[", "")
        .replace("]", "").replace(",", "").trim()
        + ")";
    }
  }

  private static int parseProgram(String string, int index, Node n) {
    boolean funcStart = false;
    final StringBuilder funcName = new StringBuilder();
    Node current = null;
    for (int i = index; i < string.length(); i++) {
      if (string.charAt(i) == '(' && !funcStart) {
        funcStart = true;
      } else if (funcStart) {
        if (current == null && (string.charAt(i) == '('
          || string.charAt(i) == ')' || string.charAt(i) == ' ')) {
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

  static int countNodes(GPNode root) {
    int num = root.children.length;
    for (int i = 0; i < root.children.length; i++) {
      num += countNodes(root.children[i]);
    }
    return num;
  }

  public static HashMultimap<String, List<String>> parseGrammar(
      String grammarFile) {
    try {
      final BufferedReader reader =
        new BufferedReader(new FileReader(grammarFile));

      final HashMultimap<String, List<String>> grammar = HashMultimap.create();

      final String node = "\\s*<([a-z-]+)>\\s*";
      // String pipe = "\\|";
      final String func = "\\(\\w+(?:" + node + ")*\\)";

      final Pattern nodePattern = Pattern.compile(node);
      final Pattern combPattern =
        Pattern.compile("(?:" + node + "|" + func + ")");

      String line;
      int lineNr = 0;
      final List<String> functions = new ArrayList<String>();
      while ((line = reader.readLine()) != null) {
        lineNr++;
        line = line.replace(" ", "");
        if (line.startsWith("#")) {
          continue;
        }
        final Scanner s = new Scanner(line);
        s.useDelimiter("::=");

        if (!s.hasNext(nodePattern)) {
          throw new IllegalArgumentException(
            "Not a valid construct at line: " + lineNr);
        }
        final String key = s.next(nodePattern);
        final Scanner sub = new Scanner(s.next());
        sub.useDelimiter("\\|");
        while (sub.hasNext()) {
          final String cur = sub.next();
          final Scanner rewrite = new Scanner(cur);
          final List<String> list = new ArrayList<String>();
          while (true) {
            final String found = rewrite.findInLine(combPattern);
            if (found == null) {
              break;
            } else if (found.matches(func)) {
              final String function = found.replace("(", "").replace(")", "");
              final Scanner funcScan = new Scanner(function);

              final String funcName = funcScan.findInLine("\\w*");
              if (funcName == null) {
                throw new IllegalArgumentException(
                  "A function must have a name. In: " + cur);
              }
              list.add(funcName);
              functions.add(function);
              while (true) {
                final String param = funcScan.findInLine(nodePattern);
                if (param != null) {
                  list.add(param);
                } else {
                  break;
                }
              }
            } else {
              list.add(found);
            }
          }
          grammar.put(key, list);

        }
      }
      return grammar;
    } catch (final Throwable t) {
      throw new RuntimeException(t);
    }
  }
}
