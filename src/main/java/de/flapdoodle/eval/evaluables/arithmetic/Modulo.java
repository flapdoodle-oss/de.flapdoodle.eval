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

public class Modulo extends TypedEvaluables.Wrapper {

	public static class Number implements TypedEvaluable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first,
			Value.NumberValue second) throws EvaluationException {

//			if (second.wrapped().equals(BigDecimal.ZERO)) {
//				throw new EvaluationException(token, "Division by zero");
//			}
//
			return Value.of(first.wrapped().remainder(second.wrapped(), evaluationContext.mathContext()));
		}
	}

	public Modulo() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(Value.NumberValue.class, Parameter.of(Value.NumberValue.class), Parameter.of(Value.NumberValue.class)
					.withValidators(NumberValidator.isNot(BigDecimal.ZERO,"Division by zero")), new Number()))
			.build());
	}
}
