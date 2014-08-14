/**
 * 
 */
package com.github.rinde.ecj;

import java.util.List;

import com.github.rinde.jppf.GPComputationResult;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;

/**
 * @author Rinde van Lon 
 * 
 */
public class GPStats extends Statistics {

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		if (state.population.subpops.length > 1) {
			throw new IllegalStateException("More than one subpop is not supported.");
		}

		Individual best_i = null; // quiets compiler complaints
		for (int y = 1; y < state.population.subpops[0].individuals.length; y++) {
			if (best_i == null || state.population.subpops[0].individuals[y].fitness.betterThan(best_i.fitness)) {
				best_i = state.population.subpops[0].individuals[y];
			}
		}

		final List<GPComputationResult> results = ((GPFitness<GPComputationResult>) best_i.fitness).getResults();
		System.out.println(results.get(0).getFitness() + " " + results.get(0).getTaskDataId());
		printMore(state, best_i, results);
	}

	public void printMore(EvolutionState state, Individual best, List<GPComputationResult> results) {}

}
