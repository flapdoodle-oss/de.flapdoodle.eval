/*
 * Copyright (C) 2023
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.eval.example.evaluables.datetime;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.example.Value;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class ZonedDateTimeParser extends TypedEvaluables.Wrapper {

    public static class FromString implements TypedEvaluable.Arg1<Value.StringValue, Value.DateTimeValue> {

        @Override
        public Value.DateTimeValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.StringValue argument) throws EvaluationException {
            return Value.of(parse(argument.wrapped(), null, evaluationContext.zoneId()));
        }
    }


    public static class WithFormat implements TypedEvaluable.Arg2<Value.StringValue, Value.StringValue, Value.DateTimeValue> {

        @Override
        public Value.DateTimeValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.StringValue argument, Value.StringValue format) throws EvaluationException {
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
                            : DateTimeFormatter.ofPattern(format).withZone(zoneId));
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(value, formatter);
            return Optional.of(zonedDateTime.toInstant());
        }
        catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    public ZonedDateTimeParser() {
        super(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.DateTimeValue.class, Value.StringValue.class, new FromString()))
                .addList(TypedEvaluable.of(Value.DateTimeValue.class, Value.StringValue.class, Value.StringValue.class, new WithFormat()))
                .build());
    }
}
