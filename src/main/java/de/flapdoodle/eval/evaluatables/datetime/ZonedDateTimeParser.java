package de.flapdoodle.eval.evaluatables.datetime;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class ZonedDateTimeParser extends TypedEvaluatables.Wrapper {

    public static class FromString implements TypedEvaluatable.Arg1<Value.StringValue, Value.DateTimeValue> {

        @Override
        public Value.DateTimeValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.StringValue argument) throws EvaluationException {
            return Value.of(parse(argument.wrapped(), null, evaluationContext.zoneId()));
        }
    }


    public static class WithFormat implements TypedEvaluatable.Arg2<Value.StringValue, Value.StringValue, Value.DateTimeValue> {

        @Override
        public Value.DateTimeValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.StringValue argument, Value.StringValue format) throws EvaluationException {
            return Value.of(parse(argument.wrapped(), format.wrapped(), evaluationContext.zoneId()));
        }
    }

    protected static Instant parse(String value, String format, ZoneId zoneId) {
        return parseZonedDateTime(value, format, zoneId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Unable to parse zoned date/time: " + value));
    }

    private static Optional<Instant> parseZonedDateTime(String value, String format, ZoneId zoneId) {
        try {
            DateTimeFormatter formatter =
                    (format == null
                            ? DateTimeFormatter.ISO_ZONED_DATE_TIME
                            : DateTimeFormatter.ofPattern(format))
                            .withZone(zoneId);
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(value, formatter);
            return Optional.of(zonedDateTime.toInstant());
        }
        catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    public ZonedDateTimeParser() {
        super(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.DateTimeValue.class, Value.StringValue.class, new FromString()))
                .addList(TypedEvaluatable.of(Value.DateTimeValue.class, Value.StringValue.class, Value.StringValue.class, new WithFormat()))
                .build());
    }
}
