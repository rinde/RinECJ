/**
 * 
 */
package com.github.rinde.ecj;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public interface Heuristic<T> {

	double compute(T input);

	String getId();

}
