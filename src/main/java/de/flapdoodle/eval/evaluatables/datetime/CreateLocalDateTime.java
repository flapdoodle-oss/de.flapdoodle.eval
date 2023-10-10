package de.flapdoodle.eval.evaluatables.datetime;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.Parameter;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.evaluatables.validation.NumberValidator;
import de.flapdoodle.eval.evaluatables.validation.ParameterValidator;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class CreateLocalDateTime extends TypedEvaluatables.Wrapper {

    public static class With3Args implements TypedEvaluatable.Arg3<Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.DateTimeValue> {
        @Override
        public Value.DateTimeValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.NumberValue second, Value.NumberValue third) throws EvaluationException {
            return dateTimeValue(evaluationContext, first.wrapped().intValue(), second.wrapped().intValue(), third.wrapped().intValue(), 0, 0, 0, 0);
        }
    }

    public static class With4Args implements TypedEvaluatable.Arg4<Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.DateTimeValue> {
        @Override
        public Value.DateTimeValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue year, Value.NumberValue month, Value.NumberValue day, Value.NumberValue hour) throws EvaluationException {
            return dateTimeValue(evaluationContext, year.wrapped().intValue(), month.wrapped().intValue(), day.wrapped().intValue(), hour.wrapped().intValue(), 0, 0, 0);
        }
    }

    public static class With5Args implements TypedEvaluatable.Arg5<Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.DateTimeValue> {
        @Override
        public Value.DateTimeValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue year, Value.NumberValue month, Value.NumberValue day, Value.NumberValue hour, Value.NumberValue minute) throws EvaluationException {
            return dateTimeValue(evaluationContext, year.wrapped().intValue(), month.wrapped().intValue(), day.wrapped().intValue(), hour.wrapped().intValue(), minute.wrapped().intValue(), 0, 0);
        }
    }

    public static class With6Args implements TypedEvaluatable.Arg6<Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.DateTimeValue> {
        @Override
        public Value.DateTimeValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue year, Value.NumberValue month, Value.NumberValue day, Value.NumberValue hour, Value.NumberValue minute, Value.NumberValue second) throws EvaluationException {
            return dateTimeValue(evaluationContext, year.wrapped().intValue(), month.wrapped().intValue(), day.wrapped().intValue(), hour.wrapped().intValue(), minute.wrapped().intValue(), second.wrapped().intValue(), 0);
        }
    }

    public static class With7Args implements TypedEvaluatable.Arg7<Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.NumberValue, Value.DateTimeValue> {
        @Override
        public Value.DateTimeValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue year, Value.NumberValue month, Value.NumberValue day, Value.NumberValue hour, Value.NumberValue minute, Value.NumberValue second, Value.NumberValue nano) throws EvaluationException {
            return dateTimeValue(evaluationContext, year.wrapped().intValue(), month.wrapped().intValue(), day.wrapped().intValue(), hour.wrapped().intValue(), minute.wrapped().intValue(), second.wrapped().intValue(), nano.wrapped().intValue());
        }
    }

    private static Value.DateTimeValue dateTimeValue(EvaluationContext evaluationContext, int year, int month, int day, int hour, int minute, int second, int nanoOfs) {
        ZoneId zoneId = evaluationContext.zoneId();
        return Value.of(
                LocalDateTime.of(year, month, day, hour, minute, second, nanoOfs)
                        .atZone(zoneId)
                        .toInstant());
    }


    public CreateLocalDateTime() {
        super(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.DateTimeValue.class,
                        Parameter.of(Value.NumberValue.class).withValidators(year()), Parameter.of(Value.NumberValue.class).withValidators(month()), Parameter.of(Value.NumberValue.class).withValidators(dayOfMonth()),
                        new With3Args()))
                .addList(TypedEvaluatable.of(Value.DateTimeValue.class,
                        Parameter.of(Value.NumberValue.class).withValidators(year()), Parameter.of(Value.NumberValue.class).withValidators(month()), Parameter.of(Value.NumberValue.class).withValidators(dayOfMonth()),
                        Parameter.of(Value.NumberValue.class).withValidators(hourOfDay()),
                        new With4Args()))
                .addList(TypedEvaluatable.of(Value.DateTimeValue.class,
                        Parameter.of(Value.NumberValue.class).withValidators(year()), Parameter.of(Value.NumberValue.class).withValidators(month()), Parameter.of(Value.NumberValue.class).withValidators(dayOfMonth()),
                        Parameter.of(Value.NumberValue.class).withValidators(hourOfDay()), Parameter.of(Value.NumberValue.class).withValidators(minutes()),
                        new With5Args()))
                .addList(TypedEvaluatable.of(Value.DateTimeValue.class,
                        Parameter.of(Value.NumberValue.class).withValidators(year()), Parameter.of(Value.NumberValue.class).withValidators(month()), Parameter.of(Value.NumberValue.class).withValidators(dayOfMonth()),
                        Parameter.of(Value.NumberValue.class).withValidators(hourOfDay()), Parameter.of(Value.NumberValue.class).withValidators(minutes()), Parameter.of(Value.NumberValue.class).withValidators(seconds()),
                        new With6Args()))
                .addList(TypedEvaluatable.of(Value.DateTimeValue.class,
                        Parameter.of(Value.NumberValue.class).withValidators(year()), Parameter.of(Value.NumberValue.class).withValidators(month()), Parameter.of(Value.NumberValue.class).withValidators(dayOfMonth()),
                        Parameter.of(Value.NumberValue.class).withValidators(hourOfDay()), Parameter.of(Value.NumberValue.class).withValidators(minutes()), Parameter.of(Value.NumberValue.class).withValidators(seconds()),
                        Parameter.of(Value.NumberValue.class).withValidators(nano()),
                        new With7Args()))
                .build());
    }

    private static ParameterValidator<Value.NumberValue> year() {
        return NumberValidator.between(0, 9999);
    }

    private static ParameterValidator<Value.NumberValue> month() {
        return NumberValidator.between(1, 12);
    }

    private static ParameterValidator<Value.NumberValue> dayOfMonth() {
        return NumberValidator.between(1, 31);
    }

    private static ParameterValidator<Value.NumberValue> hourOfDay() {
        return NumberValidator.between(0, 23);
    }

    private static ParameterValidator<Value.NumberValue> minutes() {
        return NumberValidator.between(0, 59);
    }

    private static ParameterValidator<Value.NumberValue> seconds() {
        return NumberValidator.between(0, 59);
    }

    private static ParameterValidator<Value.NumberValue> nano() {
        return NumberValidator.between(0, 999999999L);
    }
}

