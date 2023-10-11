package de.flapdoodle.eval.evaluables.basic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.evaluables.Parameter;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

public class PropertyAccess extends TypedEvaluables.Wrapper {

	public static class MapAccess implements TypedEvaluable.Arg2<Value.MapValue, Value.StringValue, Value<?>> {

		@Override
		public Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.MapValue first, Value.StringValue second)
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
