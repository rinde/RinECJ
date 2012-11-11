/**
 * 
 */
package rinde.ecj;

import java.util.List;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public interface GPComputationJob<C> /* extends ComputationJob */{

	List<GPProgram<C>> getPrograms();

	// should be unique for the programs, usually this is a dump of the programs
	// using Lisp notation
	String getId();

}
