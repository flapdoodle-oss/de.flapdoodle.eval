package de.flapdoodle.eval.example.evaluables.string;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.example.Value;

public class Contains extends TypedEvaluables.Wrapper {

    public static class Strings implements TypedEvaluable.Arg2<Value.StringValue, Value.StringValue, Value.BooleanValue> {
        @Override
        public Value.BooleanValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.StringValue first, Value.StringValue second) throws EvaluationException {
            return Value.of(first.wrapped().toLowerCase().contains(second.wrapped().toLowerCase()));
        }
    }

    public Contains() {
        super(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.StringValue.class, Value.StringValue.class, new Strings()))
                .build());
    }
}

