package de.flapdoodle.eval.evaluables.booleans;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.core.evaluables.Parameter;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.values.Value;

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
