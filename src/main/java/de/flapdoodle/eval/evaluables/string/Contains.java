package de.flapdoodle.eval.evaluables.string;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

public class Contains extends TypedEvaluables.Wrapper {

    public static class Strings implements TypedEvaluable.Arg2<Value.StringValue, Value.StringValue, Value.BooleanValue> {
        @Override
        public Value.BooleanValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.StringValue first, Value.StringValue second) throws EvaluationException {
            return Value.of(first.wrapped().toLowerCase().contains(second.wrapped().toLowerCase()));
        }
    }

    public Contains() {
        super(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.StringValue.class, Value.StringValue.class, new Strings()))
                .build());
    }
}

