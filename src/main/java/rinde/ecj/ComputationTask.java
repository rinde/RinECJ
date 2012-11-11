/**
 * 
 */
package rinde.ecj;

import org.jppf.server.protocol.JPPFTask;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public abstract class ComputationTask<R extends GPComputationResult, C> extends JPPFTask {

	protected GPProgram<C> program;

	public ComputationTask(GPProgram<C> p) {
		program = p;
	}

	public String getGPId() {
		return program.toString();
	}

	public R getComputationResult() {
		return (R) getResult();
	}
}
