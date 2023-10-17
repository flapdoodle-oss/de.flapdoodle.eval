package de.flapdoodle.eval.example.evaluables.basic;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.example.Value;

import java.math.RoundingMode;

public class Round  extends TypedEvaluables.Wrapper {

    @Deprecated
    public static class Ceiling implements TypedEvaluable.Arg1<Value.NumberValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first) throws EvaluationException {
            return Value.of(first.wrapped().setScale(0, RoundingMode.CEILING));
        }
    }

    @Deprecated
    public static class Floor implements TypedEvaluable.Arg1<Value.NumberValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first) throws EvaluationException {
            return Value.of(first.wrapped().setScale(0, RoundingMode.FLOOR));
        }
    }

    public static class Number implements TypedEvaluable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.NumberValue second) throws EvaluationException {
            return Value.of(
                    first
                            .wrapped()
                            .setScale(
                                    second.wrapped().intValue(),
                                    evaluationContext.mathContext().getRoundingMode()));

        }
    }

    public static class NumberMode implements TypedEvaluable.Arg2<Value.NumberValue, Value.StringValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.StringValue second) throws EvaluationException {
            return Value.of(first.wrapped().setScale(0, roundingMode(token, second)));
        }
    }

    public static class NumberScaleMode implements TypedEvaluable.Arg3<Value.NumberValue, Value.NumberValue, Value.StringValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.NumberValue second, Value.StringValue third) throws EvaluationException {
            return Value.of(
                    first
                            .wrapped()
                            .setScale(
                                    second.wrapped().intValue(),
                                    roundingMode(token, third)));

        }
    }

    private static RoundingMode roundingMode(Token token, Value.StringValue second) throws EvaluationException {
        RoundingMode mode=null;
        switch (second.wrapped().toLowerCase()) {
            case "ceiling":
                mode=RoundingMode.CEILING;
                break;
            case "down":
                mode=RoundingMode.DOWN;
                break;
            case "up":
                mode=RoundingMode.UP;
                break;
            case "half-down":
                mode=RoundingMode.HALF_DOWN;
                break;
            case "half-up":
                mode=RoundingMode.HALF_UP;
                break;
            case "half-even":
                mode=RoundingMode.HALF_EVEN;
                break;
            case "floor":
                mode=RoundingMode.FLOOR;
                break;
        }

        if (mode==null) {
            throw new EvaluationException(token,"unsupported rounding mode: "+ second.wrapped());
        }
        return mode;
    }

    protected Round(TypedEvaluables config) {
        super(config);
    }

    public Round() {
        this(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class, new Round.Number()))
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.StringValue.class, new Round.NumberMode()))
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class, Value.StringValue.class, new Round.NumberScaleMode()))
                .build());
    }

    @Deprecated
    public static Round ceiling() {
        return new Round(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, new Round.Ceiling()))
                .build());
    }

    @Deprecated
    public static Round floor() {
        return new Round(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, new Round.Floor()))
                .build());
    }
}
