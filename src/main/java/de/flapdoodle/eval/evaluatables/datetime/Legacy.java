package de.flapdoodle.eval.evaluatables.datetime;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;

import java.time.Instant;

public class Legacy extends TypedEvaluatables.Wrapper {

    public static class EpochFromNumber implements TypedEvaluatable.Arg1<Value.NumberValue, Value.DateTimeValue> {
        @Override
        public Value.DateTimeValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument) throws EvaluationException {
            return Value.of(Instant.ofEpochMilli(argument.wrapped().longValue()));
        }
    }

    public static class DateTime2Epoch implements TypedEvaluatable.Arg1<Value.DateTimeValue, Value.NumberValue> {
        @Override
        public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue argument) throws EvaluationException {
            return Value.of(argument.wrapped().toEpochMilli());
        }
    }


    private Legacy(TypedEvaluatables delegate) {
        super(delegate);
    }

    public static Legacy epochFromNumber() {
        return new Legacy(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.DateTimeValue.class, Value.NumberValue.class, new EpochFromNumber()))
                .build());
    }

    public static Legacy dateTime2Epoch() {
        return new Legacy(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.NumberValue.class, Value.DateTimeValue.class, new DateTime2Epoch()))
                .build());
    }

}
