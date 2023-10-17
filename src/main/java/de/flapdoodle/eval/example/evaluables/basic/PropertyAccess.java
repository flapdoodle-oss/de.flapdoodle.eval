package de.flapdoodle.eval.example.evaluables.basic;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.Parameter;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.example.Value;

public class PropertyAccess extends TypedEvaluables.Wrapper {

	public static class MapAccess implements TypedEvaluable.Arg2<Value.MapValue, Value.StringValue, Value<?>> {

		@Override
		public Value<?> evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.MapValue first, Value.StringValue second)
			throws EvaluationException {
			if (!first.wrapped().containsKey(second.wrapped())) {
				throw new EvaluationException(
					token, String.format("Field '%s' not found in structure", second.wrapped()));
			}

			return first.wrapped().get(second.wrapped());
		}
	}

	public PropertyAccess() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of((Class) Value.class, Parameter.of(Value.MapValue.class), Parameter.of(Value.StringValue.class), new MapAccess()))
			.build());
	}
}
