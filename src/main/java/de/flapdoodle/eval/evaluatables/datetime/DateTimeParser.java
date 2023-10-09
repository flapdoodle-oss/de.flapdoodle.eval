package de.flapdoodle.eval.evaluatables.datetime;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class DateTimeParser extends TypedEvaluatables.Wrapper {

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

    private static Instant parse(String value, String format, ZoneId zoneId) {
        return parseInstant(value)
                .orElseGet(() -> parseLocalDateTime(value, format, zoneId)
                        .orElseGet(() -> parseDate(value, format)
                                .orElseThrow(() -> new IllegalArgumentException("Unable to parse date/time: " + value))));
    }

    private static Optional<Instant> parseLocalDateTime(String value, String format, ZoneId zoneId) {
        try {
            DateTimeFormatter formatter =
                    (format == null
                            ? DateTimeFormatter.ISO_LOCAL_DATE_TIME
                            : DateTimeFormatter.ofPattern(format));
            return Optional.of(LocalDateTime.parse(value, formatter).atZone(zoneId).toInstant());
        }
        catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    private static Optional<Instant> parseDate(String value, String format) {
        try {
            DateTimeFormatter formatter =
                    (format == null ? DateTimeFormatter.ISO_LOCAL_DATE : DateTimeFormatter.ofPattern(format));
            LocalDate localDate = LocalDate.parse(value, formatter);
            return Optional.of(localDate.atStartOfDay().atOffset(ZoneOffset.UTC).toInstant());
        }
        catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    private static Optional<Instant> parseInstant(String value) {
        try {
            return Optional.of(Instant.parse(value));
        }
        catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    public DateTimeParser() {
        super(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.DateTimeValue.class, Value.StringValue.class, new FromString()))
                .addList(TypedEvaluatable.of(Value.DateTimeValue.class, Value.StringValue.class, Value.StringValue.class, new WithFormat()))
                .build());
    }
}
