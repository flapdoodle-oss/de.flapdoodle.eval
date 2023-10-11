package de.flapdoodle.eval.evaluables.arithmetic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.math.BigDecimal;
import java.util.List;

public class Min extends TypedEvaluables.Wrapper {

	public static class Number implements TypedEvaluable.VarArg1<Value.NumberValue, Value.NumberValue> {
		@Override
		public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, List<Value.NumberValue> arguments) throws EvaluationException {
			BigDecimal max = null;
			for (Value.NumberValue parameter : arguments) {
				if (max == null || parameter.wrapped().compareTo(max) < 0) {
					max = parameter.wrapped();
				}
			}
			return Value.of(max);
		}
	}

	public Min() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.ofVarArg(Value.NumberValue.class, Value.NumberValue.class, new Number()))
			.build());
	}
}
