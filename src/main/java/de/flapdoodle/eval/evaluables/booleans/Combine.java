package de.flapdoodle.eval.evaluables.booleans;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.VariableResolver;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;

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
