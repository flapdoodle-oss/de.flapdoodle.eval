package de.flapdoodle.eval.evaluatables.booleans;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.Parameter;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;

public class Not extends TypedEvaluatables.Wrapper {
    public static class Bool implements TypedEvaluatable.Arg1<Value.BooleanValue, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.BooleanValue argument) throws EvaluationException {
            return Value.of(!argument.wrapped());
        }
    }

    public Not() {
        super(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.BooleanValue.class, Parameter.of(Value.BooleanValue.class), new Not.Bool()))
                .build());
    }
}
