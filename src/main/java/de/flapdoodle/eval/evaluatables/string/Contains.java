package de.flapdoodle.eval.evaluatables.string;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;

public class Contains extends TypedEvaluatables.Wrapper {

    public static class Strings implements TypedEvaluatable.Arg2<Value.StringValue, Value.StringValue, Value.BooleanValue> {
        @Override
        public Value.BooleanValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.StringValue first, Value.StringValue second) throws EvaluationException {
            return Value.of(first.wrapped().toLowerCase().contains(second.wrapped().toLowerCase()));
        }
    }

    public Contains() {
        super(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.BooleanValue.class, Value.StringValue.class, Value.StringValue.class, new Strings()))
                .build());
    }
}

