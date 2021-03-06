/**
 *
 */
package com.github.rinde.ecj;

import java.io.Serializable;

/**
 * immutable.
 *
 * @author Rinde van Lon
 *
 */
public class GPProgram<C> implements Serializable, PriorityHeuristic<C> {

  private static final long serialVersionUID = 2873071674972923971L;
  protected final GPFuncNode<C> root;

  public GPProgram(GPFuncNode<C> rootNode) {
    root = rootNode;
  }

  @Override
  public double compute(C context) {
    return executeNode(root, context);
  }

  protected double executeNode(GPFuncNode<C> current, C context) {
    final double[] vals = new double[current.getNumChildren()];
    for (int i = 0; i < vals.length; i++) {
      vals[i] = executeNode(current.getChild(i), context);
    }
    return current.getFunction().execute(vals, context);
  }

  @Override
  public String toString() {
    return GPProgramParser.toLisp(this);
  }

  @Override
  public GPProgram<C> clone() {
    return new GPProgram<>(root);
  }

  @Override
  public String getId() {
    return toString();
  }
}
