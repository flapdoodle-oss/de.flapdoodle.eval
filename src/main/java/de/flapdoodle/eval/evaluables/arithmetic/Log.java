package de.flapdoodle.eval.evaluables.arithmetic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluables.Parameter;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.evaluables.validation.NumberValidator;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.math.BigDecimal;

public class Log extends TypedEvaluables.Wrapper {

	public static class Number implements TypedEvaluable.Arg1<Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument)
			throws EvaluationException {
			double d = argument.wrapped().doubleValue();

			return Value.of(Math.log(d));
		}
	}

	public Log() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(
				Value.NumberValue.class,
				Parameter.of(Value.NumberValue.class)
					.withValidators(NumberValidator.greaterThan(BigDecimal.ZERO)),
				new Number()))
			.build());
	}
}
