package de.flapdoodle.eval.evaluatables.datetime;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatDateTime extends TypedEvaluatables.Wrapper {

    public static class ToString implements TypedEvaluatable.Arg1<Value.DateTimeValue, Value.StringValue> {

        @Override
        public Value.StringValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue dateTime) throws EvaluationException {
            LocalDateTime dateTimeValue = dateTime.wrapped().atZone(evaluationContext.zoneId()).toLocalDateTime();
            return Value.of(dateTimeValue.toString());
        }
    }

    public static class FormatString implements TypedEvaluatable.Arg2<Value.DateTimeValue, Value.StringValue, Value.StringValue> {

        @Override
        public Value.StringValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue dateTime, Value.StringValue format) throws EvaluationException {
            LocalDateTime dateTimeValue = dateTime.wrapped().atZone(evaluationContext.zoneId()).toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format.wrapped());
            return Value.of(dateTimeValue.format(formatter));
        }
    }

    public FormatDateTime() {
        super(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.StringValue.class, Value.DateTimeValue.class, new ToString()))
                .addList(TypedEvaluatable.of(Value.StringValue.class, Value.DateTimeValue.class, Value.StringValue.class, new FormatString()))
                .build());
    }
}
