package de.flapdoodle.eval.doc;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;

public class ArrayAccess extends TypedEvaluables.Wrapper {

	private static class CharAt implements TypedEvaluable.Arg2<String, Integer, String> {
		@Override
		public String evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, String first, Integer second)
			throws EvaluationException {
			return "" + first.charAt(second);
		}
	}

	public ArrayAccess() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(String.class, String.class, Integer.class, new CharAt()))
			.build());
	}
}
