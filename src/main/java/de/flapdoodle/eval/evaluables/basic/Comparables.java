package de.flapdoodle.eval.evaluables.basic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.util.function.Predicate;

public class Comparables extends TypedEvaluables.Wrapper {

    private Comparables(TypedEvaluables delegate) {
        super(delegate);
    }

    private static class Compare<T extends Comparable<T>, V extends Value.ComparableValue<T>> implements TypedEvaluable.Arg2<V, V, Value.BooleanValue> {

        private final Predicate<Integer> check;

        public Compare(Predicate<Integer> check) {
            this.check = check;
        }

        @Override
        public Value.BooleanValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, V first, V second) throws EvaluationException {
            return Value.of(check.test(first.compareTo(second)));
        }
    }

    public static class Greater<T extends Comparable<T>, V extends Value.ComparableValue<T>> extends Compare<T, V> {

        public Greater() {
            super(it -> it > 0);
        }
    }

    public static class GreaterOrEqual<T extends Comparable<T>, V extends Value.ComparableValue<T>> extends Compare<T, V> {

        public GreaterOrEqual() {
            super(it -> it >= 0);
        }
    }

    public static class Less<T extends Comparable<T>, V extends Value.ComparableValue<T>> extends Compare<T, V> {

        public Less() {
            super(it -> it < 0);
        }
    }

    public static class LessOrEqual<T extends Comparable<T>, V extends Value.ComparableValue<T>> extends Compare<T, V> {

        public LessOrEqual() {
            super(it -> it <= 0);
        }
    }

    public static Comparables less() {
        return new Comparables(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.NumberValue.class, Value.NumberValue.class, new Less<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.StringValue.class, Value.StringValue.class, new Less<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DateTimeValue.class, Value.DateTimeValue.class, new Less<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DurationValue.class, Value.DurationValue.class, new Less<>()))
                .build());
    }

    public static Comparables lessOrEqual() {
        return new Comparables(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.NumberValue.class, Value.NumberValue.class, new LessOrEqual<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.StringValue.class, Value.StringValue.class, new LessOrEqual<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DateTimeValue.class, Value.DateTimeValue.class, new LessOrEqual<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DurationValue.class, Value.DurationValue.class, new LessOrEqual<>()))
                .build());
    }

    public static Comparables greater() {
        return new Comparables(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.NumberValue.class, Value.NumberValue.class, new Greater<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.StringValue.class, Value.StringValue.class, new Greater<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DateTimeValue.class, Value.DateTimeValue.class, new Greater<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DurationValue.class, Value.DurationValue.class, new Greater<>()))
                .build());
    }

    public static Comparables greaterOrEqual() {
        return new Comparables(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.NumberValue.class, Value.NumberValue.class, new GreaterOrEqual<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.StringValue.class, Value.StringValue.class, new GreaterOrEqual<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DateTimeValue.class, Value.DateTimeValue.class, new GreaterOrEqual<>()))
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.DurationValue.class, Value.DurationValue.class, new GreaterOrEqual<>()))
                .build());
    }
}
