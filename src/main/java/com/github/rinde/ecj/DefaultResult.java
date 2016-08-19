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
    return fitness;
  }

  @Override
  public String getTaskDataId() {
    return taskDataId;
  }

}
