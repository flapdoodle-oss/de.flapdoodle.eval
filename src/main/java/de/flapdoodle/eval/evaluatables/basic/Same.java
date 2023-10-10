package de.flapdoodle.eval.evaluatables.basic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

public class Same extends TypedEvaluatables.Wrapper {

    public static class AnyType<A extends Value<?>, B extends Value<?>> implements TypedEvaluatable.Arg2<A, B, Value.BooleanValue> {

        @Override
        public Value.BooleanValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, A first, B second) throws EvaluationException {
            return Value.of(first.wrapped() == second.wrapped());
        }
    }

    public Same() {
        super(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.BooleanValue.class, Value.class, Value.class, new AnyType<>()))
                .build());
    }
}
