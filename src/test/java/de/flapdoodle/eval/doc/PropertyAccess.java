package de.flapdoodle.eval.doc;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;

import java.util.Map;

public class PropertyAccess extends TypedEvaluables.Wrapper {

	private static class MapValue implements TypedEvaluable.Arg2<Map, String, Object> {
		@Override
		public Object evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Map first, String second)
			throws EvaluationException {
			return first.get(second);
		}
	}

	public PropertyAccess() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(Object.class, Map.class, String.class, new MapValue()))
			.build());
	}
}
