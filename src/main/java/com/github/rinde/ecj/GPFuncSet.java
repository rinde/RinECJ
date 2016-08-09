/**
 *
 */
package com.github.rinde.ecj;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import ec.EvolutionState;
import ec.gp.GPFunctionSet;
import ec.gp.GPInitializer;
import ec.gp.GPNode;
import ec.gp.GPType;
import ec.util.Parameter;

/**
 * @author Rinde van Lon
 *
 */
public abstract class GPFuncSet<T> extends GPFunctionSet {

  private static final long serialVersionUID = 4450863070617016798L;
  private final Collection<GPFunc<T>> functions;

  public GPFuncSet() {
    functions = create();
  }

  public abstract Collection<GPFunc<T>> create();

  @Override
  public final void setup(final EvolutionState state, final Parameter base) {
    // What's my name?
    name = state.parameters.getString(base.push(P_NAME), null);
    if (name == null) {
      state.output.fatal("No name was given for this function set.",
        base.push(P_NAME));
    }
    // Register me
    final GPFunctionSet oldFunctionset =
      (GPFunctionSet) (((GPInitializer) state.initializer).functionSetRepository
        .put(name, this));
    if (oldFunctionset != null) {
      state.output
        .fatal(
          "The GPFunctionSet \"" + name + "\" has been defined multiple times.",
          base.push(P_NAME));
    }

    nodesByName = new Hashtable();

    final Parameter p = base.push(P_FUNC);
    final Vector tmp = new Vector();
    int j = 0;
    for (final GPFunc<?> func : functions) {
      final GPBaseNode<?> node = new GPBaseNode(func);
      nodesByName.put(func.name(), new GPNode[] {node});
      final Parameter pp = p.push("" + j);
      j++;
      node.setup(state, pp);
      tmp.addElement(node);
    }

    // Make my hash tables
    nodes_h = new Hashtable();
    terminals_h = new Hashtable();
    nonterminals_h = new Hashtable();

    // Now set 'em up according to the types in GPType

    final Enumeration e =
      ((GPInitializer) state.initializer).typeRepository.elements();
    final GPInitializer initializer = (GPInitializer) state.initializer;
    while (e.hasMoreElements()) {
      final GPType typ = (GPType) (e.nextElement());

      // make vectors for the type.
      final Vector nodesV = new Vector();
      final Vector terminalsV = new Vector();
      final Vector nonterminalsV = new Vector();

      // add GPNodes as appropriate to each vector
      final Enumeration v = tmp.elements();
      while (v.hasMoreElements()) {
        final GPNode i = (GPNode) (v.nextElement());
        if (typ.compatibleWith(initializer,
          i.constraints(initializer).returntype)) {
          nodesV.addElement(i);
          if (i.children.length == 0) {
            terminalsV.addElement(i);
          } else {
            nonterminalsV.addElement(i);
          }
        }
      }

      // turn nodes_h' vectors into arrays
      GPNode[] ii = new GPNode[nodesV.size()];
      nodesV.copyInto(ii);
      nodes_h.put(typ, ii);

      // turn terminals_h' vectors into arrays
      ii = new GPNode[terminalsV.size()];
      terminalsV.copyInto(ii);
      terminals_h.put(typ, ii);

      // turn nonterminals_h' vectors into arrays
      ii = new GPNode[nonterminalsV.size()];
      nonterminalsV.copyInto(ii);
      nonterminals_h.put(typ, ii);
    }

    // I don't check to see if the generation mechanism will be valid here
    // -- I check that in GPTreeConstraints, where I can do the weaker check
    // of going top-down through functions rather than making sure that
    // every
    // single function has a compatible argument function (an unneccessary
    // check)

    // because I promised when I called:
    state.output.exitIfErrors();

    // postprocess the function set
    postProcessFunctionSet();
  }
}
