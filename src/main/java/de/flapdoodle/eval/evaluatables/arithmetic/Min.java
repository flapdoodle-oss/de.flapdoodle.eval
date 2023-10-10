package de.flapdoodle.eval.evaluatables.arithmetic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.math.BigDecimal;
import java.util.List;

public class Min extends TypedEvaluatables.Wrapper {

	public static class Number implements TypedEvaluatable.VarArg1<Value.NumberValue, Value.NumberValue> {
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
		super(TypedEvaluatables.builder()
			.addList(TypedEvaluatable.ofVarArg(Value.NumberValue.class, Value.NumberValue.class, new Number()))
			.build());
	}
}
