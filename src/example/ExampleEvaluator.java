/**
 * 
 */
package rinde.ecj.example;

import static java.util.Arrays.asList;

import java.util.Collection;

import org.jppf.task.storage.DataProvider;

import com.github.rinde.ecj.DefaultResult;
import com.github.rinde.ecj.GPBaseNode;
import com.github.rinde.ecj.GPEvaluator;
import com.github.rinde.ecj.GPProgram;
import com.github.rinde.ecj.GPProgramParser;
import com.github.rinde.jppf.ComputationTask;

import rinde.ecj.example.ExampleEvaluator.ExampleContext;
import rinde.ecj.example.ExampleEvaluator.ExampleTask;
import ec.EvolutionState;
import ec.gp.GPTree;

/**
 * @author Rinde van Lon 
 * 
 */
public class ExampleEvaluator extends GPEvaluator<ExampleTask, DefaultResult, GPProgram<ExampleContext>> {

	@Override
	protected Collection<ExampleTask> createComputationJobs(DataProvider dataProvider, GPTree[] trees,
			EvolutionState state) {
		final GPProgram<ExampleContext> prog = GPProgramParser
				.convertToGPProgram((GPBaseNode<ExampleContext>) trees[0].child);
		return asList(new ExampleTask(prog));
	}

	@Override
	protected int expectedNumberOfResultsPerGPIndividual(EvolutionState state) {
		return 1;
	}

	public static class ExampleTask extends ComputationTask<DefaultResult, GPProgram<ExampleContext>> {

		public ExampleTask(GPProgram<ExampleContext> p) {
			super(p);
		}

		public void run() {
			double diff = 0;
			for (int x = 0; x < 10; x++) {
				for (int y = 0; y < 10; y++) {
					final double goal = (x * x) + y;
					final double result = taskData.compute(new ExampleContext(x, y));
					diff += Math.abs(goal - result);
				}
			}
			float fitness = (float) diff;
			if (Float.isInfinite(fitness) || Float.isNaN(fitness)) {
				fitness = Float.MAX_VALUE;
			}
			setResult(new DefaultResult(fitness, taskData.getId()));
		}
	}

	public static class ExampleContext {
		public final int x;
		public final int y;

		public ExampleContext(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
