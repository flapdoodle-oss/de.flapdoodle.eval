package de.flapdoodle.eval.evaluatables.datetime;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.time.Duration;

public class DurationParser extends TypedEvaluatables.Wrapper {

    public static class Strings implements TypedEvaluatable.Arg1<Value.StringValue, Value.DurationValue> {
        @Override
        public Value.DurationValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.StringValue argument) throws EvaluationException {
            return Value.of(Duration.parse(argument.wrapped()));
        }
    }

    public static class OfDays implements TypedEvaluatable.Arg1<Value.NumberValue, Value.DurationValue> {
        @Override
        public Value.DurationValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument) throws EvaluationException {
            return Value.of(Duration.ofDays(argument.wrapped().longValue()));
        }
    }

    public static class OfMillis implements TypedEvaluatable.Arg1<Value.NumberValue, Value.DurationValue> {
        @Override
        public Value.DurationValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument) throws EvaluationException {
            return Value.of(Duration.ofMillis(argument.wrapped().longValue()));
        }
    }

    private DurationParser(TypedEvaluatables evaluatables) {
        super(evaluatables);
    }

    public DurationParser() {
        this(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.DurationValue.class, Value.StringValue.class, new Strings()))
                .build());
    }

    @Deprecated
    public static DurationParser parseDuration() {
        return new DurationParser(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.DurationValue.class, Value.StringValue.class, new Strings()))
                .build());
    }
    @Deprecated
    public static DurationParser ofDays() {
        return new DurationParser(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.DurationValue.class, Value.NumberValue.class, new OfDays()))
                .build());
    }
    @Deprecated
    public static DurationParser ofMillis() {
        return new DurationParser(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.DurationValue.class, Value.NumberValue.class, new OfMillis()))
                .build());
    }
}
