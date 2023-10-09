package de.flapdoodle.eval.evaluatables.arithmetic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public class Random extends TypedEvaluatables.Wrapper {

    public static class Number implements TypedEvaluatable.Arg0<Value.NumberValue> {
        SecureRandom secureRandom = new SecureRandom();

        @Override
        public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token)
                throws EvaluationException {
            return Value.of(secureRandom.nextDouble());
        }
    }

    public static class NumberRange implements TypedEvaluatable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {
        @Override
        public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.NumberValue second) throws EvaluationException {
            return Value.of(ThreadLocalRandom.current().nextDouble(first.wrapped().doubleValue(), second.wrapped().doubleValue()));
        }
    }

    public Random() {
        super(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.NumberValue.class, new Number()))
                .addList(TypedEvaluatable.of(Value.NumberValue.class,Value.NumberValue.class,Value.NumberValue.class, new NumberRange()))
                .build());
    }
}
