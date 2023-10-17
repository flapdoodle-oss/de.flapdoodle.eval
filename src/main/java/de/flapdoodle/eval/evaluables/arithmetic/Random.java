package de.flapdoodle.eval.evaluables.arithmetic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.VariableResolver;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public class Random extends TypedEvaluables.Wrapper {

    public static class Number implements TypedEvaluable.Arg0<Value.NumberValue> {
        SecureRandom secureRandom = new SecureRandom();

        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token)
                throws EvaluationException {
            return Value.of(secureRandom.nextDouble());
        }
    }

    public static class NumberRange implements TypedEvaluable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {
        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.NumberValue second) throws EvaluationException {
            return Value.of(ThreadLocalRandom.current().nextDouble(first.wrapped().doubleValue(), second.wrapped().doubleValue()));
        }
    }

    public Random() {
        super(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.NumberValue.class, new Number()))
                .addList(TypedEvaluable.of(Value.NumberValue.class,Value.NumberValue.class,Value.NumberValue.class, new NumberRange()))
                .build());
    }
}
