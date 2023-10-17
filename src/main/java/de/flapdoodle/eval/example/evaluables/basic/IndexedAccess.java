package de.flapdoodle.eval.example.evaluables.basic;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.Parameter;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.example.Value;

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
