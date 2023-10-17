package de.flapdoodle.eval.doc;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;

public class Minus extends TypedEvaluables.Wrapper {

	public static class Int implements TypedEvaluable.Arg2<Integer, Integer, Integer> {

		@Override
		public Integer evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Integer first,
			Integer second) throws EvaluationException {
			return first - second;
		}
	}

	public static class Negate implements TypedEvaluable.Arg1<Integer, Integer> {

		@Override
		public Integer evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Integer first) throws EvaluationException {
			return -first;
		}
	}

	public Minus() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(Integer.class, Integer.class, Integer.class, new Int()))
			.addList(TypedEvaluable.of(Integer.class, Integer.class, new Negate()))
			.build());
	}
}
