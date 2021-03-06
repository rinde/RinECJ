/**
 *
 */
package com.github.rinde.ecj;

import java.io.Serializable;

import javax.annotation.Nullable;

/**
 * @author Rinde van Lon
 *
 */
public abstract class GPFunc<C> implements Serializable {
  private static final long serialVersionUID = -861693143274097130L;
  private final int numChildren;
  private final String name;

  public GPFunc() {
    this(0);
  }

  public GPFunc(int children) {
    this(null, children);
  }

  public GPFunc(String nm) {
    this(nm, 0);
  }

  public GPFunc(String nm, int children) {
    numChildren = children;
    if (nm == null) {
      this.name = getClass().getSimpleName().toLowerCase();
    } else {
      this.name = nm;
    }
  }

  public int getNumChildren() {
    return numChildren;
  }

  @Override
  public String toString() {
    return name;
  }

  public String name() {
    return name;
  }

  public GPFunc<C> create() {
    try {
      return this.getClass().getConstructor().newInstance();
    } catch (final Exception e) {
      throw new IllegalStateException(
        "In order for this to work each GPFunc instance must have a publicly "
          + "accessible zero-arg constructor. Typically the instances are inner"
          + " public static classes.",
        e);
    }
  }

  public abstract double execute(@Nullable double[] input, C context);
}
