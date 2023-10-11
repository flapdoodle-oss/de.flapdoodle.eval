package de.flapdoodle.eval.evaluables.arithmetic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.math.BigDecimal;

public class Factorial extends TypedEvaluables.Wrapper {

    public static class Number implements TypedEvaluable.Arg1<Value.NumberValue, Value.NumberValue> {

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
        super(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, new Number()))
                .build());
    }
}
