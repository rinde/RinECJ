/**
 *
 */
package com.github.rinde.ecj;

import static com.github.rinde.ecj.GenericFunctions.newAdd;
import static com.github.rinde.ecj.GenericFunctions.newConstants;
import static com.github.rinde.ecj.GenericFunctions.newDiv;
import static com.github.rinde.ecj.GenericFunctions.newIf4;
import static com.github.rinde.ecj.GenericFunctions.newMul;
import static com.github.rinde.ecj.GenericFunctions.newPow;
import static com.github.rinde.ecj.GenericFunctions.newSub;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

/**
 * @author Rinde van Lon
 *
 */
public class TestGPProgram {

  static final List<GPFunc<Object>> FUNC_LIST = init();
  private static final double PRECISION = 0.0000001;

  public static List<GPFunc<Object>> init() {
    final List<GPFunc<Object>> list =
      newConstants(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    list.addAll(
      asList(newIf4(), newAdd(), newSub(), newMul(), newPow(), newDiv()));
    return list;
  }

  @Test
  public void test() {

    // final GPBaseNode<Object> node =
    // GPProgramParser.parseProgramFunc("(sub 1.0 0.0)", functionList);
    //
    // final GPProgram<Object> prog =
    // GPProgramCreator.convertToGPProgram(node, Object.class);
    // System.out.println(GPProgramParser.toLisp(prog));

    assertFunc(1, "(- 1.0 0.0)");
    assertFunc(216, "(pow (/ (- (x 10.0 2.0) 8.0) 2.0) 3.0)");
    assertFunc(4, "(pow 2.0 2.0)");

  }

  public void assertFunc(double expectedValue, String func) {
    assertEquals(expectedValue,
      GPProgramParser.parseProgramFunc(func, FUNC_LIST).compute(null),
      PRECISION);
  }
}
