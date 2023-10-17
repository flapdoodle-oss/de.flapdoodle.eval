package de.flapdoodle.eval.evaluables.datetime;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.VariableResolver;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;

import java.time.Duration;

public class DurationParser extends TypedEvaluables.Wrapper {

    public static class Strings implements TypedEvaluable.Arg1<Value.StringValue, Value.DurationValue> {
        @Override
        public Value.DurationValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.StringValue argument) throws EvaluationException {
            return Value.of(Duration.parse(argument.wrapped()));
        }
    }

    public static class OfDays implements TypedEvaluable.Arg1<Value.NumberValue, Value.DurationValue> {
        @Override
        public Value.DurationValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument) throws EvaluationException {
            return Value.of(Duration.ofDays(argument.wrapped().longValue()));
        }
    }

    public static class OfMillis implements TypedEvaluable.Arg1<Value.NumberValue, Value.DurationValue> {
        @Override
        public Value.DurationValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument) throws EvaluationException {
            return Value.of(Duration.ofMillis(argument.wrapped().longValue()));
        }
    }

    private DurationParser(TypedEvaluables evaluatables) {
        super(evaluatables);
    }

    public DurationParser() {
        this(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.DurationValue.class, Value.StringValue.class, new Strings()))
                .build());
    }

    @Deprecated
    public static DurationParser parseDuration() {
        return new DurationParser(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.DurationValue.class, Value.StringValue.class, new Strings()))
                .build());
    }
    @Deprecated
    public static DurationParser ofDays() {
        return new DurationParser(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.DurationValue.class, Value.NumberValue.class, new OfDays()))
                .build());
    }
    @Deprecated
    public static DurationParser ofMillis() {
        return new DurationParser(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.DurationValue.class, Value.NumberValue.class, new OfMillis()))
                .build());
    }
}
