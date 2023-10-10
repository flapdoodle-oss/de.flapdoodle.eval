package de.flapdoodle.eval.evaluatables.string;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

public class ToLowerCase extends TypedEvaluatables.Wrapper {

    public static class Strings implements TypedEvaluatable.Arg1<Value.StringValue, Value.StringValue> {
        @Override
        public Value.StringValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.StringValue argument) throws EvaluationException {
            return Value.of(argument.wrapped().toLowerCase());
        }
    }

    public ToLowerCase() {
        super(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.StringValue.class, Value.StringValue.class, new Strings()))
                .build());
    }
}

