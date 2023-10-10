package de.flapdoodle.eval.evaluatables.basic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.Parameter;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

public class PropertyAccess extends TypedEvaluatables.Wrapper {

	public static class MapAccess implements TypedEvaluatable.Arg2<Value.MapValue, Value.StringValue, Value<?>> {

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
		super(TypedEvaluatables.builder()
			.addList(TypedEvaluatable.of((Class) Value.class, Parameter.of(Value.MapValue.class), Parameter.of(Value.StringValue.class), new MapAccess()))
			.build());
	}
}
