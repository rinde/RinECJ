/*
 * Copyright (C) 2011-2016 Rinde van Lon, iMinds-DistriNet, KU Leuven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rinde.ecj;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Map.Entry;

import com.github.rinde.jppf.GPComputationResult;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPTree;
import ec.util.Parameter;

/**
 *
 * @author Rinde van Lon
 */
public abstract class BaseEvaluator extends Evaluator {

  @Override
  public void setup(final EvolutionState state, final Parameter base) {}

  @Override
  public abstract void evaluatePopulation(EvolutionState state);

  @Override
  public boolean runComplete(EvolutionState state) {
    return false;
  }

  @Override
  public void initializeContacts(EvolutionState state) {}

  @Override
  public void reinitializeContacts(EvolutionState state) {}

  @Override
  public void closeContacts(EvolutionState state, int result) {}

  public static String treeToString(GPTree[] t) {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < t.length; i++) {
      sb.append(t[i].child.makeLispTree());
    }
    return sb.toString();
  }

  protected SetMultimap<GPNodeHolder, IndividualHolder> getGPFitnessMapping(
      EvolutionState state) {
    final SetMultimap<GPNodeHolder, IndividualHolder> mapping =
      LinkedHashMultimap.create();
    for (int i = 0; i < state.population.subpops.length; i++) {
      for (int j = 0; j < state.population.subpops[i].individuals.length; j++) {
        final GPIndividual gpInd =
          ((GPIndividual) state.population.subpops[i].individuals[j]);
        mapping.put(new GPNodeHolder(gpInd.trees), new IndividualHolder(gpInd));
      }
    }
    return mapping;
  }

  protected abstract int expectedNumberOfResultsPerGPIndividual(
      EvolutionState state);

  protected void processResults(EvolutionState state,
      Multimap<GPNodeHolder, IndividualHolder> mapping,
      Collection<? extends GPComputationResult> results) {
    final Multimap<String, GPComputationResult> gatheredFitnessValues =
      HashMultimap.create();
    for (final GPComputationResult res : results) {
      final String programString = res.getTaskDataId();// res.getComputationJob().((J)
      gatheredFitnessValues.put(programString, res);
    }
    for (final Entry<String, Collection<GPComputationResult>> entry : gatheredFitnessValues
      .asMap().entrySet()) {
      if (entry.getValue()
        .size() != expectedNumberOfResultsPerGPIndividual(state)) {
        throw new IllegalStateException(
          "Number of received results does not match the number of expected results! received: "
            + entry.getValue().size() + " expected: "
            + expectedNumberOfResultsPerGPIndividual(state) + " for " + entry);
      }

      float sum = 0;
      boolean notGood = false;
      for (final GPComputationResult res : entry.getValue()) {
        if (res.getFitness() == Float.MAX_VALUE) {
          notGood = true;
        }
        sum += res.getFitness();
      }
      if (notGood) {
        sum = Float.MAX_VALUE;
      } else {
        sum /= expectedNumberOfResultsPerGPIndividual(state);
      }
      final Collection<IndividualHolder> inds =
        mapping.get(new GPNodeHolder(entry.getKey()));
      checkState(!inds.isEmpty(),
        "there must be at least one individual for every program");
      for (final IndividualHolder ind : inds) {
        ((GPFitness<GPComputationResult>) ind.ind.fitness)
          .addResults(entry.getValue());
        ((GPFitness<GPComputationResult>) ind.ind.fitness)
          .setStandardizedFitness(state, sum);
        ind.ind.evaluated = true;
      }
    }

  }

  public class IndividualHolder {
    public final Individual ind;

    public IndividualHolder(Individual ind) {
      this.ind = ind;
    }
  }

  public class GPNodeHolder {
    public final String string;
    public final GPTree[] trees;

    public GPNodeHolder(GPTree[] t, String s) {
      trees = t;
      string = s;
    }

    public GPNodeHolder(GPTree[] t) {
      this(t, BaseEvaluator.treeToString(t));
    }

    public GPNodeHolder(String string) {
      this(null, string);
    }

    @Override
    public int hashCode() {
      return string.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      return toString().equals(o.toString());
    }

    @Override
    public String toString() {
      return string;
    }
  }
}
