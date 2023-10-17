package de.flapdoodle.eval.example.evaluables.validation;

import de.flapdoodle.eval.core.exceptions.EvaluableException;
import de.flapdoodle.eval.core.validation.ParameterValidator;
import de.flapdoodle.eval.example.Value;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Predicate;

public class NumberValidator implements ParameterValidator<Value.NumberValue> {

    private final Predicate<BigDecimal> test;
    private final String errorMessageTemplate;

    private NumberValidator(Predicate<BigDecimal> test, String errorMessageTemplate) {
        this.test = test;
        this.errorMessageTemplate = errorMessageTemplate;
    }

    @Override
    public Optional<EvaluableException> validate(Value.NumberValue parameterValue) {
        if (!test.test(parameterValue.wrapped())) {
            return Optional.of(new EvaluableException(errorMessageTemplate.replace("%s", parameterValue.wrapped().toString())));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return test.toString();
    }

    public static NumberValidator isNot(BigDecimal value, String errorMessageTemplate) {
        return new NumberValidator(withDescription(v -> v.compareTo(value) != 0,"value != "+value), errorMessageTemplate);
    }

    public static NumberValidator isNot(BigDecimal value) {
        return isNot(value, "value of "+value+" is invalid");
    }

    public static NumberValidator greaterOrEqualThan(BigDecimal value, String errorMessageTemplate) {
        return new NumberValidator(withDescription(v -> v.compareTo(value) >= 0,"value >= "+value), errorMessageTemplate);
    }

    public static NumberValidator greaterOrEqualThan(BigDecimal value) {
        return greaterOrEqualThan(value, "value is not >= "+value+": %s");
    }

    public static NumberValidator greaterThan(BigDecimal value, String errorMessageTemplate) {
        return new NumberValidator(withDescription(v -> v.compareTo(value) > 0, "value > " + value), errorMessageTemplate);
    }

    public static NumberValidator greaterThan(BigDecimal value) {
        return greaterThan(value, "value is not > " + value + ": %s");
    }

    public static NumberValidator smallerOrEqualThan(BigDecimal value, String errorMessageTemplate) {
        return new NumberValidator(withDescription(v -> v.compareTo(value) <= 0,"value <= "+value), errorMessageTemplate);
    }

    public static NumberValidator smallerOrEqualThan(BigDecimal value) {
        return smallerOrEqualThan(value, "value is not <= "+value+": %s");
    }

    public static NumberValidator smallerThan(BigDecimal value, String errorMessageTemplate) {
        return new NumberValidator(withDescription(v -> v.compareTo(value) < 0,"value < "+value), errorMessageTemplate);
    }

    public static NumberValidator smallerThan(BigDecimal value) {
        return smallerThan(value, "value is not < "+value+": %s");
    }

    public static NumberValidator between(long min, long max) {
        return new NumberValidator(withDescription(v -> {
            return v.longValue() >= min && v.longValue() <= max;
        }, min+" <= value <= "+max), "value is not between "+min+" and "+max+": %s");
    }

    private static <T> Predicate<T> withDescription(Predicate<T> wrapped, String toString) {
        return new Predicate<T>() {
            @Override
            public boolean test(T v) {
                return wrapped.test(v);
            }

            @Override
            public String toString() {
                return toString;
            }
        };
    }
}
