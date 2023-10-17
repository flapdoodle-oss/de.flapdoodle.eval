package de.flapdoodle.eval.evaluables.basic;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.core.evaluables.Parameter;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.values.Value;

public class Conditional extends TypedEvaluables.Wrapper {

    public static class IfTrue<SUB extends Value<?>> implements TypedEvaluable.Arg3<Value.BooleanValue, SUB, SUB, SUB> {

        @Override
        public SUB evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.BooleanValue first, SUB second, SUB third) throws EvaluationException {
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
