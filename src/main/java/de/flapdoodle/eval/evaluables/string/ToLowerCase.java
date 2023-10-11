package de.flapdoodle.eval.evaluables.string;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

public class ToLowerCase extends TypedEvaluables.Wrapper {

    public static class Strings implements TypedEvaluable.Arg1<Value.StringValue, Value.StringValue> {
        @Override
        public Value.StringValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.StringValue argument) throws EvaluationException {
            return Value.of(argument.wrapped().toLowerCase());
        }
    }

    public ToLowerCase() {
        super(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.StringValue.class, Value.StringValue.class, new Strings()))
                .build());
    }
}

