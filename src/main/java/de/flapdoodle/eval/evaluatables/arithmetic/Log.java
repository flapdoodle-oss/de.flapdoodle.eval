package de.flapdoodle.eval.evaluatables.arithmetic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.Parameter;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.evaluatables.validation.NumberValidator;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.math.BigDecimal;

public class Log extends TypedEvaluatables.Wrapper {

	public static class Number implements TypedEvaluatable.Arg1<Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument)
			throws EvaluationException {
			double d = argument.wrapped().doubleValue();

			return Value.of(Math.log(d));
		}
	}

	public Log() {
		super(TypedEvaluatables.builder()
			.addList(TypedEvaluatable.of(
				Value.NumberValue.class,
				Parameter.of(Value.NumberValue.class)
					.withValidators(NumberValidator.greaterThan(BigDecimal.ZERO)),
				new Number()))
			.build());
	}
}
