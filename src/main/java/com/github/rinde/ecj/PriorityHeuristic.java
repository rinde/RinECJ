/**
 * 
 */
package com.github.rinde.ecj;

/**
 * @author Rinde van Lon 
 * 
 */
public interface PriorityHeuristic<T> {

	double compute(T input);

	String getId();

}
