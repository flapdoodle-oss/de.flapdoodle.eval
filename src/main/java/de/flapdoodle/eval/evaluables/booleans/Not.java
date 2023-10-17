package de.flapdoodle.eval.evaluables.booleans;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.evaluables.Parameter;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.VariableResolver;

public class Not extends TypedEvaluables.Wrapper {
    public static class Bool implements TypedEvaluable.Arg1<Value.BooleanValue, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.BooleanValue argument) throws EvaluationException {
            return Value.of(!argument.wrapped());
        }
    }

    public Not() {
        super(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Parameter.of(Value.BooleanValue.class), new Not.Bool()))
                .build());
    }
}
