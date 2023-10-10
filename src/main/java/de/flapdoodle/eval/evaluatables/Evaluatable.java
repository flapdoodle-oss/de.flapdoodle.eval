package de.flapdoodle.eval.evaluatables;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.util.List;

public interface Evaluatable<T extends Value<?>> {
    T evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, List<? extends Value<?>> arguments) throws EvaluationException;

    default Evaluatable<T> named(String name) {
        Evaluatable<T> that = this;
        return new Evaluatable<T>() {
            @Override
            public T evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, List<? extends Value<?>> arguments)
              throws EvaluationException {
                return that.evaluate(valueResolver,evaluationContext,token,arguments);
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }
}
