package de.flapdoodle.eval.evaluables.string;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.values.Value;

public class ToLowerCase extends TypedEvaluables.Wrapper {

    public static class Strings implements TypedEvaluable.Arg1<Value.StringValue, Value.StringValue> {
        @Override
        public Value.StringValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.StringValue argument) throws EvaluationException {
            return Value.of(argument.wrapped().toLowerCase());
        }
    }

    public ToLowerCase() {
        super(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.StringValue.class, Value.StringValue.class, new Strings()))
                .build());
    }
}

