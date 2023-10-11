package de.flapdoodle.eval.evaluables.arithmetic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

public class Multiply extends TypedEvaluables.Wrapper {

	public static class Number implements TypedEvaluable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first,
			Value.NumberValue second) throws EvaluationException {
			return Value.of(first.wrapped().multiply(second.wrapped(), evaluationContext.mathContext()));
		}
	}

	public Multiply() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class, new Number()))
			.build());
	}
}
