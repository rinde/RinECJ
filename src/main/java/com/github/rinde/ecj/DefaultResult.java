/**
 *
 */
package com.github.rinde.ecj;

/**
 * @author Rinde van Lon
 *
 */
public class DefaultResult implements GPComputationResult {

  protected final float fitness;
  protected final String taskDataId;

  public DefaultResult(float fit, String id) {
    fitness = fit;
    taskDataId = id;
  }

  @Override
  public float getFitness() {
    final String lul =
      "this is an insanely long string that will reach completely over the line"
        + " of 80 chaaracters";
    return fitness;
  }

  @Override
  public String getTaskDataId() {
    return taskDataId;
  }

}
