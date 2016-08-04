/**
 *
 */
package com.github.rinde.ecj;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.server.protocol.JPPFTask;
import org.jppf.task.storage.DataProvider;
import org.jppf.task.storage.MemoryMapDataProvider;

import com.github.rinde.jppf.ComputationTask;
import com.github.rinde.jppf.GPComputationResult;
import com.google.common.collect.Multimap;

import ec.EvolutionState;
import ec.gp.GPTree;
import ec.util.Parameter;

/**
 * @author Rinde van Lon
 *
 */
// note: the C here must correspond to the type of the GPFuncSet !
public abstract class GPEvaluator<T extends ComputationTask<R, C>, R extends GPComputationResult, C>
    extends BaseEvaluator {

  private static final long serialVersionUID = -8172136113716773085L;
  public final static String P_HOST = "host";

  protected transient JPPFClient jppfClient;

  public enum ComputationStrategy {
    LOCAL, DISTRIBUTED
  }

  protected ComputationStrategy compStrategy;

  @Override
  public void setup(final EvolutionState state, final Parameter base) {

    final String hostName = state.parameters.getString(base.push(P_HOST), null);
    if (hostName == null || hostName.equalsIgnoreCase("local")) {
      compStrategy = ComputationStrategy.LOCAL;
    } else {
      compStrategy = ComputationStrategy.DISTRIBUTED;
      jppfClient = new JPPFClient();
    }
  }

  @Override
  public void evaluatePopulation(EvolutionState state) {
    // for (final Individual ind : state.population.subpops[0].individuals)
    // {
    // final GPIndividual gpInd = (GPIndividual) ind;
    // System.out.println(gpInd.trees[0].child.makeLispTree());
    // }

    final Multimap<GPNodeHolder, IndividualHolder> mapping =
      getGPFitnessMapping(state);
    final DataProvider dataProvider = new MemoryMapDataProvider();
    final JPPFJob job = new JPPFJob(dataProvider);
    job.setBlocking(true);
    job.setName("Generation " + state.generation);

    try {
      for (final GPNodeHolder key : mapping.keySet()) {
        final Collection<T> coll =
          createComputationJobs(dataProvider, key.trees, state);
        for (final T j : coll) {
          // only needed when local, otherwise JPPF handles this
          // automatically
          if (compStrategy == ComputationStrategy.LOCAL) {
            j.setDataProvider(dataProvider);
          }
          job.addTask(j);
        }
      }
      final Collection<R> results = compute(job);
      processResults(state, mapping, results);
    } catch (final Exception e) {
      throw new WrapException(e);
    }
  }

  public static class WrapException extends RuntimeException {

    public final Exception exception;

    public WrapException(Exception ex) {
      exception = ex;
    }

    @Override
    public void printStackTrace() {
      exception.printStackTrace();
    }

    @Override
    public String toString() {
      return exception.toString();
    }

  }

  protected Collection<R> compute(JPPFJob job) throws Exception {
    // either use JPPF or compute locally
    final Collection<R> results = newArrayList();
    if (compStrategy == ComputationStrategy.LOCAL) {
      for (final JPPFTask task : job.getTasks()) {
        task.run();
        results.add(((T) task).getComputationResult());
      }
    } else {
      final List<JPPFTask> res = jppfClient.submit(job);
      for (final JPPFTask t : res) {
        if (t.getException() != null) {
          throw t.getException();// new
          // RuntimeException("This exception occured on a node",
          // t.getException());
        }
        results.add(((T) t).getComputationResult());
      }
    }
    return results;
  }

  protected abstract Collection<T> createComputationJobs(
      DataProvider dataProvider, GPTree[] trees,
      EvolutionState state);

  @Override
  protected abstract int expectedNumberOfResultsPerGPIndividual(
      EvolutionState state);

  @Override
  public boolean runComplete(EvolutionState state) {
    // TODO Auto-generated method stub
    return false;
  }

}
