package de.flapdoodle.eval.evaluables.basic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.VariableResolver;
import de.flapdoodle.eval.evaluables.Parameter;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;

public class IndexedAccess extends TypedEvaluables.Wrapper {

	public static class ValueArrayAccess implements TypedEvaluable.Arg2<Value.ArrayValue, Value.NumberValue, Value<?>> {

		@Override
		public Value<?> evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.ArrayValue first, Value.NumberValue second)
			throws EvaluationException {
			return first.wrapped().get(second.wrapped().intValue());
		}
	}

	public static class StringAccess implements TypedEvaluable.Arg2<Value.StringValue, Value.NumberValue, Value<?>> {
		@Override
		public Value<?> evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.StringValue first, Value.NumberValue second)
			throws EvaluationException {
			return Value.of(""+first.wrapped().charAt(second.wrapped().intValue()));
		}
	}

	public IndexedAccess() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of((Class) Value.class, Parameter.of(Value.ArrayValue.class), Parameter.of(Value.NumberValue.class), new ValueArrayAccess()))
			.addList(TypedEvaluable.of((Class) Value.class, Parameter.of(Value.StringValue.class), Parameter.of(Value.NumberValue.class), new StringAccess()))
			.addList(TypedEvaluable.of((Class) Value.class, Parameter.of(Value.MapValue.class), Parameter.of(Value.StringValue.class), new PropertyAccess.MapAccess()))
			.build());
	}
}
