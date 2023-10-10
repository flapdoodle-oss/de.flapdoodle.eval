package de.flapdoodle.eval.evaluatables.basic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.math.RoundingMode;

public class Round  extends TypedEvaluatables.Wrapper {

    @Deprecated
    public static class Ceiling implements TypedEvaluatable.Arg1<Value.NumberValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first) throws EvaluationException {
            return Value.of(first.wrapped().setScale(0, RoundingMode.CEILING));
        }
    }

    @Deprecated
    public static class Floor implements TypedEvaluatable.Arg1<Value.NumberValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first) throws EvaluationException {
            return Value.of(first.wrapped().setScale(0, RoundingMode.FLOOR));
        }
    }

    public static class Number implements TypedEvaluatable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.NumberValue second) throws EvaluationException {
            return Value.of(
                    first
                            .wrapped()
                            .setScale(
                                    second.wrapped().intValue(),
                                    evaluationContext.mathContext().getRoundingMode()));

        }
    }

    public static class NumberMode implements TypedEvaluatable.Arg2<Value.NumberValue, Value.StringValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.StringValue second) throws EvaluationException {
            return Value.of(first.wrapped().setScale(0, roundingMode(token, second)));
        }
    }

    public static class NumberScaleMode implements TypedEvaluatable.Arg3<Value.NumberValue, Value.NumberValue, Value.StringValue, Value.NumberValue> {

        @Override
        public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first, Value.NumberValue second, Value.StringValue third) throws EvaluationException {
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

    protected Round(TypedEvaluatables config) {
        super(config);
    }

    public Round() {
        this(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class, new Round.Number()))
                .addList(TypedEvaluatable.of(Value.NumberValue.class, Value.NumberValue.class, Value.StringValue.class, new Round.NumberMode()))
                .addList(TypedEvaluatable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class, Value.StringValue.class, new Round.NumberScaleMode()))
                .build());
    }

    @Deprecated
    public static Round ceiling() {
        return new Round(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.NumberValue.class, Value.NumberValue.class, new Round.Ceiling()))
                .build());
    }

    @Deprecated
    public static Round floor() {
        return new Round(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.NumberValue.class, Value.NumberValue.class, new Round.Floor()))
                .build());
    }
}
