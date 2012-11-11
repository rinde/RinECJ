package rinde.ecj.example;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

import rinde.ecj.GPFunc;
import rinde.ecj.GPFuncSet;
import rinde.ecj.GenericFunctions.Add;
import rinde.ecj.GenericFunctions.Constant;
import rinde.ecj.GenericFunctions.Div;
import rinde.ecj.GenericFunctions.If4;
import rinde.ecj.GenericFunctions.Mul;
import rinde.ecj.GenericFunctions.Pow;
import rinde.ecj.GenericFunctions.Sub;
import rinde.ecj.example.ExampleEvaluator.ExampleContext;

public class ExampleFunctions extends GPFuncSet<ExampleContext> {
	@SuppressWarnings("unchecked")
	@Override
	public Collection<GPFunc<ExampleContext>> create() {
		return newArrayList(
		/* GENERIC FUNCTIONS */
		new If4<ExampleContext>(), /* */
				new Add<ExampleContext>(), /* */
				new Sub<ExampleContext>(), /* */
				new Div<ExampleContext>(), /* */
				new Mul<ExampleContext>(), /* */
				new Pow<ExampleContext>(),
				/* CONSTANTS */
				new Constant<ExampleContext>(1), /* */
				new Constant<ExampleContext>(0), /* */
				/* PROBLEM SPECIFIC VARIABLES */
				new X(), new Y());

	}

	public static class X extends GPFunc<ExampleContext> {
		@Override
		public double execute(double[] input, ExampleContext context) {
			return context.x;
		}
	}

	public static class Y extends GPFunc<ExampleContext> {
		@Override
		public double execute(double[] input, ExampleContext context) {
			return context.y;
		}
	}

}