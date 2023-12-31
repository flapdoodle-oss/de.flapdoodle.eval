package de.flapdoodle.eval.example.evaluables.basic;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluableByNumberOfArguments;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.example.Value;

import java.util.Objects;

public class Equals extends TypedEvaluables.Wrapper {

    public static class AnyType<A extends Value<?>, B extends Value<?>> implements TypedEvaluable.Arg2<A, B, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second) throws EvaluationException {
            return Value.of(Objects.equals(first.wrapped(), second.wrapped()));
        }
    }

    public static class AnyTypeNot<A extends Value<?>, B extends Value<?>> implements TypedEvaluable.Arg2<A, B, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second) throws EvaluationException {
            return Value.of(!Objects.equals(first.wrapped(), second.wrapped()));
        }
    }

    private Equals(TypedEvaluables delegate) {
        super(delegate);
    }

    public Equals() {
        this(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.class, Value.class, new AnyType<>()))
                .build());
    }

    public static TypedEvaluableByNumberOfArguments not() {
        return new Equals(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.class, Value.class, new AnyTypeNot<>()))
                .build());
    }
}
