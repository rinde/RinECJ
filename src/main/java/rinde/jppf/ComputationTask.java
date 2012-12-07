/**
 * 
 */
package rinde.jppf;

import org.jppf.server.protocol.JPPFTask;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
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
