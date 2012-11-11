/**
 * 
 */
package rinde.ecj;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public class DefaultResult implements GPComputationResult {

	protected final float fitness;
	protected final String gpId;

	public DefaultResult(float fit, String id) {
		fitness = fit;
		gpId = id;
	}

	public float getFitness() {
		return fitness;
	}

	public String getGPId() {
		return gpId;
	}
}
