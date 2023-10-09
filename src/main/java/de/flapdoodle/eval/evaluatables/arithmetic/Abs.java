package de.flapdoodle.eval.evaluatables.arithmetic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;

public class Abs extends TypedEvaluatables.Wrapper {

	public static class Number implements TypedEvaluatable.Arg1<Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first) throws EvaluationException {

			return Value.of(first.wrapped().abs(evaluationContext.mathContext()));
		}
	}

	public Abs() {
		super(TypedEvaluatables.builder()
			.addList(TypedEvaluatable.of(Value.NumberValue.class, Value.NumberValue.class, new Number()))
			.build());
	}
}
