/**
 *
 */
package com.github.rinde.ecj;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

/**
 *
 *
 * @author Rinde van Lon
 *
 */
public final class GenericFunctions {
  private GenericFunctions() {}

  public static <T> If4<T> newIf4() {
    return new If4<T>();
  }

  public static <T> Add<T> newAdd() {
    return new Add<T>();
  }

  public static <T> Sub<T> newSub() {
    return new Sub<T>();
  }

  public static <T> Mul<T> newMul() {
    return new Mul<T>();
  }

  public static <T> Div<T> newDiv() {
    return new Div<T>();
  }

  public static <T> Pow<T> newPow() {
    return new Pow<T>();
  }

  public static <T> Constant<T> newConstant(double v) {
    return new Constant<T>(v);
  }

  @SuppressWarnings("unchecked")
  public static <T> List<GPFunc<T>> newConstants(double... vs) {
    final List<GPFunc<T>> list = newArrayList();
    for (final double d : vs) {
      list.add((Constant<T>) newConstant(d));
    }
    return list;
  }

  public static class If4<T> extends GPFunc<T> {
    private static final long serialVersionUID = -8010536154981009677L;

    public If4() {
      super(4);
    }

    @Override
    public double execute(double[] input, T context) {
      return input[0] < input[1] ? input[2] : input[3];
    }

  }

  public static class Add<T> extends GPFunc<T> {
    private static final long serialVersionUID = 2200299240321191164L;

    public Add() {
      super("+", 2);
    }

    @Override
    public double execute(double[] input, T context) {
      return input[0] + input[1];
    }
  }

  public static class Sub<T> extends GPFunc<T> {
    private static final long serialVersionUID = -1363621468791103104L;

    public Sub() {
      super("-", 2);
    }

    @Override
    public double execute(double[] input, T context) {
      return input[0] - input[1];
    }
  }

  public static class Mul<T> extends GPFunc<T> {
    private static final long serialVersionUID = 537369514239069421L;

    public Mul() {
      super("x", 2);
    }

    @Override
    public double execute(double[] input, T context) {
      return input[0] * input[1];
    }
  }

  public static class Div<T> extends GPFunc<T> {
    private static final long serialVersionUID = 6727402143693804260L;

    public Div() {
      super("/", 2);
    }

    // protected division
    @Override
    public double execute(double[] input, T context) {
      final double val0 = input[0];
      final double val1 = input[1];
      if (val1 == 0d) {
        return 1d;
      }
      return val0 / val1;
    }
  }

  public static class Pow<T> extends GPFunc<T> {
    private static final long serialVersionUID = -1207160233965775202L;

    public Pow() {
      super(2);
    }

    @Override
    public double execute(double[] input, T context) {
      final double res = Math.pow(input[0], input[1]);
      if (Double.isInfinite(res) || Double.isNaN(res)) {
        return 1d;
      }
      return res;
    }
  }

  public static class Neg<T> extends GPFunc<T> {

    public Neg() {
      super(1);
    }

    @Override
    public double execute(double[] input, T context) {
      return -1d * input[0];
    }
  }

  public static class Min<T> extends GPFunc<T> {

    public Min() {
      super(2);
    }

    @Override
    public double execute(double[] input, T context) {
      return Math.min(input[0], input[1]);
    }
  }

  public static class Max<T> extends GPFunc<T> {

    public Max() {
      super(2);
    }

    @Override
    public double execute(double[] input, T context) {
      return Math.max(input[0], input[1]);
    }
  }

  public static class Constant<C> extends GPFunc<C> {

    private static final long serialVersionUID = -2428773869358609217L;
    private final double value;

    public Constant(double val) {
      super("" + val, 0);
      value = val;
    }

    @Override
    public double execute(double[] input, C context) {
      return value;
    }

    @Override
    public GPFunc<C> create() {
      return new Constant<C>(value);
    }

  }

}
