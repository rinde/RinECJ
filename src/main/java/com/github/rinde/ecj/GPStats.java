/**
 *
 */
package com.github.rinde.ecj;

import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;

/**
 * @author Rinde van Lon
 *
 */
public class GPStats extends Statistics {

  private static final long serialVersionUID = 8418481677963130011L;

  public GPStats() {}

  @Override
  public void postEvaluationStatistics(final EvolutionState state) {
    super.postEvaluationStatistics(state);

    if (state.population.subpops.length > 1) {
      throw new IllegalStateException("More than one subpop is not supported.");
    }

    Individual bestInd = null;
    for (int y = 1; y < state.population.subpops[0].individuals.length; y++) {
      if (bestInd == null || state.population.subpops[0].individuals[y].fitness
        .betterThan(bestInd.fitness)) {
        bestInd = state.population.subpops[0].individuals[y];
      }
    }

    final GPFitness<GPComputationResult> fitn =
      (GPFitness<GPComputationResult>) bestInd.fitness;
    final List<GPComputationResult> bestResults = fitn.getResults();
    System.out.println(
      fitn.standardizedFitness() + " " + bestResults.get(0).getTaskDataId());
    printMore(state, bestInd, bestResults);
  }

  public void printMore(EvolutionState state, Individual best,
      List<GPComputationResult> bestResults) {}

}
