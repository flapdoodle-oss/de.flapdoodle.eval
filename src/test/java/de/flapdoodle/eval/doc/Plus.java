package de.flapdoodle.eval.doc;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.example.Value;

import java.math.BigDecimal;
import java.util.List;

public class Plus extends TypedEvaluables.Wrapper {

	public static class Int implements TypedEvaluable.Arg2<Integer, Integer, Integer> {

		@Override
		public Integer evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Integer first,
			Integer second) throws EvaluationException {
			return first + second;
		}
	}

	public static class ToString<A, B> implements TypedEvaluable.Arg2<A, B, String> {

		@Override
		public String evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first,
										  B second) throws EvaluationException {
			return "" + first + second;
		}
	}

	public static class Sum implements TypedEvaluable.VarArg1<Integer, Integer> {

		@Override
		public Integer evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, List<Integer> arguments)
			throws EvaluationException {
			int sum = 0;
			for (Integer parameter : arguments) {
				sum = sum + parameter;
			}
			return sum;
		}
	}

	public Plus() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(Integer.class, Integer.class, Integer.class, new Int()))
			.addList(TypedEvaluable.ofVarArg(Integer.class, Integer.class, new Sum()))
			.addList(TypedEvaluable.of(String.class, Object.class, Object.class, new ToString<>()))
			.build());
	}
}
