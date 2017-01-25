package com.github.rinde.ecj;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import com.github.rinde.ecj.GenericFunctions.Add;
import com.github.rinde.ecj.GenericFunctions.Constant;
import com.github.rinde.ecj.GenericFunctions.Div;
import com.github.rinde.ecj.GenericFunctions.If4;
import com.github.rinde.ecj.GenericFunctions.Max;
import com.github.rinde.ecj.GenericFunctions.Min;
import com.github.rinde.ecj.GenericFunctions.Mul;
import com.github.rinde.ecj.GenericFunctions.Neg;
import com.github.rinde.ecj.GenericFunctions.Pow;
import com.github.rinde.ecj.GenericFunctions.Sub;

public class ToDotTest {

  @Test
  public void test() {
    final String prog =
      "(x (x (max (- insertionovertime insertionflexibility) (min insertioncost insertioncost)) (if4 (x (x (max (- insertionovertime insertionflexibility) insertioncost) (if4 (max (- (max (- (+ insertionovertime deliveryurgency) insertionflexibility) (pow insertiontardiness 2.0)) insertionflexibility) routelength) (- insertionovertime insertionflexibility) (+ (max (max (if4 2.0 mado (neg 10.0) (neg insertioncost)) (min insertioncost insertioncost)) routelength) deliveryurgency) (x (pow 10.0 ado) (max (if4 (pow insertiontardiness (min insertioncost (- (- insertionovertime insertionflexibility) insertionflexibility))) (if4 (max (- (+ insertionovertime deliveryurgency) insertionflexibility) routelength) (+ insertionovertime deliveryurgency) (pow 10.0 insertioncost) 10.0) (- insertioncost insertionflexibility) (x routelength insertioncost)) (min insertioncost insertioncost))))) (x (x (max insertiontraveltime routelength) insertiontardiness) insertionflexibility)) (neg 10.0) (x (max (- (+ insertionovertime (pow 10.0 insertiontraveltime)) insertionflexibility) routelength) insertiontardiness) (pow 10.0 ado))) (x (max (- (+ insertionovertime deliveryurgency) insertionflexibility) (pow insertiontardiness 2.0)) (pow 10.0 insertioncost)))";

    final GPProgram<Object> progObj =
      GPProgramParser.parseProgramFunc(prog, create());
    System.out.println(GPProgramParser.toDot(progObj));
  }

  @Test
  public void test2() {
    final String prog =
      "(x (max (- (+ insertionovertime deliveryurgency) insertionflexibility) (pow insertiontardiness 2.0)) (pow 10.0 insertioncost))";
    final GPProgram<Object> progObj =
      GPProgramParser.parseProgramFunc(prog, create());
    System.out.println(GPProgramParser.toDot(progObj));
  }

  public Collection<GPFunc<Object>> create() {
    return Arrays.asList(
      /* GENERIC FUNCTIONS */
      new If4<Object>(),
      new Add<Object>(),
      new Sub<Object>(),
      new Div<Object>(),
      new Mul<Object>(),
      new Pow<Object>(),
      new Neg<Object>(),
      new Min<Object>(),
      new Max<Object>(),
      /* CONSTANTS */
      new Constant<Object>(10),
      new Constant<Object>(2),
      new Constant<Object>(1),
      new Constant<Object>(0),
      /* VARIABLES */
      new Terminal("InsertionFlexibility"),
      new Terminal("InsertionCost"),
      new Terminal("InsertionTravelTime"),
      new Terminal("InsertionTardiness"),
      new Terminal("InsertionOverTime"),
      new Terminal("TimeLeft"),
      new Terminal("Slack"),
      new Terminal("Ado"),
      new Terminal("Mido"),
      new Terminal("Mado"),
      new Terminal("RouteLength"),
      new Terminal("PickupUrgency"),
      new Terminal("DeliveryUrgency"));
  }

  static class Terminal extends GPFunc<Object> {
    Terminal(String nm) {
      super(nm.toLowerCase());
    }

    @Override
    public double execute(double[] input, Object context) {
      // TODO Auto-generated method stub
      return 0;
    }
  }

}
