package de.flapdoodle.eval.evaluables.datetime;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.values.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatDateTime extends TypedEvaluables.Wrapper {

    public static class ToString implements TypedEvaluable.Arg1<Value.DateTimeValue, Value.StringValue> {

        @Override
        public Value.StringValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue dateTime) throws EvaluationException {
            LocalDateTime dateTimeValue = dateTime.wrapped().atZone(evaluationContext.zoneId()).toLocalDateTime();
            return Value.of(dateTimeValue.toString());
        }
    }

    public static class FormatString implements TypedEvaluable.Arg2<Value.DateTimeValue, Value.StringValue, Value.StringValue> {

        @Override
        public Value.StringValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue dateTime, Value.StringValue format) throws EvaluationException {
            LocalDateTime dateTimeValue = dateTime.wrapped().atZone(evaluationContext.zoneId()).toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format.wrapped());
            return Value.of(dateTimeValue.format(formatter));
        }
    }

    public FormatDateTime() {
        super(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.StringValue.class, Value.DateTimeValue.class, new ToString()))
                .addList(TypedEvaluable.of(Value.StringValue.class, Value.DateTimeValue.class, Value.StringValue.class, new FormatString()))
                .build());
    }
}
