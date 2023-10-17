package de.flapdoodle.eval.example.evaluables.arithmetic;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.Parameter;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.example.Value;
import de.flapdoodle.eval.example.evaluables.validation.NumberValidator;

import java.math.BigDecimal;

public class Log10 extends TypedEvaluables.Wrapper {

	public static class Number implements TypedEvaluable.Arg1<Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument)
			throws EvaluationException {
			double d = argument.wrapped().doubleValue();

			return Value.of(Math.log10(d));
		}
	}

	public Log10() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(
				Value.NumberValue.class,
				Parameter.of(Value.NumberValue.class)
					.withValidators(NumberValidator.greaterThan(BigDecimal.ZERO)),
				new Number()))
			.build());
	}
}
