package de.flapdoodle.eval.evaluatables.basic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.Parameter;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

public class Conditional extends TypedEvaluatables.Wrapper {

    public static class IfTrue<SUB extends Value<?>> implements TypedEvaluatable.Arg3<Value.BooleanValue, SUB, SUB, SUB> {

        @Override
        public SUB evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.BooleanValue first, SUB second, SUB third) throws EvaluationException {
            return first.wrapped() ? second : third;
        }
    }

    public Conditional() {
        super(TypedEvaluatables.builder()
                .addList(TypedEvaluatable.of(Value.class, Parameter.of(Value.BooleanValue.class), Parameter.lazyWith(Value.class), Parameter.lazyWith(Value.class), new IfTrue<>()))
                .build());
    }
}
