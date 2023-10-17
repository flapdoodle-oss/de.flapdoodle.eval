package de.flapdoodle.eval.evaluables.datetime;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.VariableResolver;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;

import java.time.Instant;

public class Legacy extends TypedEvaluables.Wrapper {

    public static class EpochFromNumber implements TypedEvaluable.Arg1<Value.NumberValue, Value.DateTimeValue> {
        @Override
        public Value.DateTimeValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument) throws EvaluationException {
            return Value.of(Instant.ofEpochMilli(argument.wrapped().longValue()));
        }
    }

    public static class DateTime2Epoch implements TypedEvaluable.Arg1<Value.DateTimeValue, Value.NumberValue> {
        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue argument) throws EvaluationException {
            return Value.of(argument.wrapped().toEpochMilli());
        }
    }


    private Legacy(TypedEvaluables delegate) {
        super(delegate);
    }

    public static Legacy epochFromNumber() {
        return new Legacy(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.DateTimeValue.class, Value.NumberValue.class, new EpochFromNumber()))
                .build());
    }

    public static Legacy dateTime2Epoch() {
        return new Legacy(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.DateTimeValue.class, new DateTime2Epoch()))
                .build());
    }

}
