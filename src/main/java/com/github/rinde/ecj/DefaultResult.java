/**
 * 
 */
package com.github.rinde.ecj;

import com.github.rinde.jppf.GPComputationResult;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public class DefaultResult implements GPComputationResult {

	protected final float fitness;
	protected final String taskDataId;

	public DefaultResult(float fit, String id) {
		fitness = fit;
		taskDataId = id;
	}

	public float getFitness() {
		return fitness;
	}

	public String getTaskDataId() {
		return taskDataId;
	}

}
