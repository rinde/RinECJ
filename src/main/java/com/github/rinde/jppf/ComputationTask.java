/**
 * 
 */
package com.github.rinde.jppf;

import org.jppf.server.protocol.JPPFTask;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
// TODO investigate wether the second type parameter can be dropped (the data)
// storing data in any kind of format should be entirely left to subclasses
public abstract class ComputationTask<R extends GPComputationResult, C> extends JPPFTask {

	protected final C taskData;

	public ComputationTask(C data) {
		taskData = data;
	}

	public C getTaskData() {
		return taskData;
	}

	public R getComputationResult() {
		return (R) getResult();
	}
}
