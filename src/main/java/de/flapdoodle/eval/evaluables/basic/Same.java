package de.flapdoodle.eval.evaluables.basic;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.values.Value;

public class Same extends TypedEvaluables.Wrapper {

    public static class AnyType<A extends Value<?>, B extends Value<?>> implements TypedEvaluable.Arg2<A, B, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second) throws EvaluationException {
            return Value.of(first.wrapped() == second.wrapped());
        }
    }

    public Same() {
        super(TypedEvaluables.builder()
                .addList(TypedEvaluable.of(Value.BooleanValue.class, Value.class, Value.class, new AnyType<>()))
                .build());
    }
}
