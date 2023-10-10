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

public class Modulo extends TypedEvaluatables.Wrapper {

	public static class Number implements TypedEvaluatable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {

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
		super(TypedEvaluatables.builder()
			.addList(TypedEvaluatable.of(Value.NumberValue.class, Parameter.of(Value.NumberValue.class), Parameter.of(Value.NumberValue.class)
					.withValidators(NumberValidator.isNot(BigDecimal.ZERO,"Division by zero")), new Number()))
			.build());
	}
}
