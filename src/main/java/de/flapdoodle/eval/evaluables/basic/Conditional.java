package de.flapdoodle.eval.evaluables.basic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluables.Parameter;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

public class Conditional extends TypedEvaluables.Wrapper {

    public static class IfTrue<SUB extends Value<?>> implements TypedEvaluable.Arg3<Value.BooleanValue, SUB, SUB, SUB> {

        @Override
        public SUB evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.BooleanValue first, SUB second, SUB third) throws EvaluationException {
            return first.wrapped() ? second : third;
        }
    }

    public Conditional() {
        super(TypedEvaluables.builder()
                .addList(
									TypedEvaluable.of(Value.class, Parameter.of(Value.BooleanValue.class), Parameter.lazyWith(Value.class), Parameter.lazyWith(Value.class), new IfTrue<>()))
                .build());
    }
}
