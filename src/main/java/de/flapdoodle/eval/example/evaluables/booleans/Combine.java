package de.flapdoodle.eval.example.evaluables.booleans;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.example.Value;

public class Combine extends TypedEvaluables.Wrapper {
    public static class And implements TypedEvaluable.Arg2<Value.BooleanValue, Value.BooleanValue, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.BooleanValue first, Value.BooleanValue second) throws EvaluationException {
            return Value.of(first.wrapped() && second.wrapped());
        }
    }

    public static class Or implements TypedEvaluable.Arg2<Value.BooleanValue, Value.BooleanValue, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.BooleanValue first, Value.BooleanValue second) throws EvaluationException {
            return Value.of(first.wrapped() || second.wrapped());
        }
    }

    private Combine(TypedEvaluables delegate) {
        super(delegate);
    }

    public static Combine and() {
        return new Combine(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.BooleanValue.class, Value.BooleanValue.class, new And()))
                .build());
    }

    public static Combine or() {
        return new Combine(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.BooleanValue.class, Value.BooleanValue.class, new Or()))
                .build());
    }
}
