package de.flapdoodle.eval.evaluatables.arithmetic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.math.BigDecimal;

public class Factorial extends TypedEvaluatables.Wrapper {

    public static class Number implements TypedEvaluatable.Arg1<Value.NumberValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument) throws EvaluationException {
            int number = argument.wrapped().intValue();
            BigDecimal factorial = BigDecimal.ONE;
            for (int i = 1; i <= number; i++) {
                factorial =
                        factorial.multiply(new BigDecimal(i, evaluationContext.mathContext()), evaluationContext.mathContext());
            }
            return Value.of(factorial);
        }
    }

    public Factorial() {
        super(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.NumberValue.class, Value.NumberValue.class, new Number()))
                .build());
    }
}
