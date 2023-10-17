package de.flapdoodle.eval.evaluables.arithmetic;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.core.evaluables.Parameter;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.evaluables.validation.NumberValidator;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.values.Value;

import java.math.BigDecimal;

public class Modulo extends TypedEvaluables.Wrapper {

	public static class Number implements TypedEvaluable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first,
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
