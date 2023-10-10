package de.flapdoodle.eval.evaluatables.basic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.Parameter;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

public class IndexedAccess extends TypedEvaluatables.Wrapper {

	public static class ValueArrayAccess implements TypedEvaluatable.Arg2<Value.ArrayValue, Value.NumberValue, Value<?>> {

		@Override
		public Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.ArrayValue first, Value.NumberValue second)
			throws EvaluationException {
			return first.wrapped().get(second.wrapped().intValue());
		}
	}

	public static class StringAccess implements TypedEvaluatable.Arg2<Value.StringValue, Value.NumberValue, Value<?>> {
		@Override
		public Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.StringValue first, Value.NumberValue second)
			throws EvaluationException {
			return Value.of(""+first.wrapped().charAt(second.wrapped().intValue()));
		}
	}

	public IndexedAccess() {
		super(TypedEvaluatables.builder()
			.addList(TypedEvaluatable.of((Class) Value.class, Parameter.of(Value.ArrayValue.class), Parameter.of(Value.NumberValue.class), new ValueArrayAccess()))
			.addList(TypedEvaluatable.of((Class) Value.class, Parameter.of(Value.StringValue.class), Parameter.of(Value.NumberValue.class), new StringAccess()))
			.addList(TypedEvaluatable.of((Class) Value.class, Parameter.of(Value.MapValue.class), Parameter.of(Value.StringValue.class), new PropertyAccess.MapAccess()))
			.build());
	}
}
