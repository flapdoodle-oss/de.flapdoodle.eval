package de.flapdoodle.eval.evaluatables.booleans;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;

public class Combine extends TypedEvaluatables.Wrapper {
    public static class And implements TypedEvaluatable.Arg2<Value.BooleanValue, Value.BooleanValue, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.BooleanValue first, Value.BooleanValue second) throws EvaluationException {
            return Value.of(first.wrapped() && second.wrapped());
        }
    }

    public static class Or implements TypedEvaluatable.Arg2<Value.BooleanValue, Value.BooleanValue, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.BooleanValue first, Value.BooleanValue second) throws EvaluationException {
            return Value.of(first.wrapped() || second.wrapped());
        }
    }

    private Combine(TypedEvaluatables delegate) {
        super(delegate);
    }

    public static Combine and() {
        return new Combine(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.BooleanValue.class, Value.BooleanValue.class, Value.BooleanValue.class, new And()))
                .build());
    }

    public static Combine or() {
        return new Combine(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.BooleanValue.class, Value.BooleanValue.class, Value.BooleanValue.class, new Or()))
                .build());
    }
}
