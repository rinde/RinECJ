/**
 * 
 */
package com.github.rinde.ecj;

/**
 * @author Rinde van Lon 
 * 
 */
public interface Heuristic<T> {

	double compute(T input);

	String getId();

}
