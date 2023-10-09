package de.flapdoodle.eval.evaluatables.basic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatableByNumberOfArguments;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;

import java.util.Objects;

public class Equals extends TypedEvaluatables.Wrapper {

    public static class AnyType<A extends Value<?>, B extends Value<?>> implements TypedEvaluatable.Arg2<A, B, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, A first, B second) throws EvaluationException {
            return Value.of(Objects.equals(first.wrapped(), second.wrapped()));
        }
    }

    public static class AnyTypeNot<A extends Value<?>, B extends Value<?>> implements TypedEvaluatable.Arg2<A, B, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, A first, B second) throws EvaluationException {
            return Value.of(!Objects.equals(first.wrapped(), second.wrapped()));
        }
    }

    private Equals(TypedEvaluatables delegate) {
        super(delegate);
    }

    public Equals() {
        this(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.BooleanValue.class, Value.class, Value.class, new AnyType<>()))
                .build());
    }

    public static TypedEvaluatableByNumberOfArguments not() {
        return new Equals(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.BooleanValue.class, Value.class, Value.class, new AnyTypeNot<>()))
                .build());
    }
}
