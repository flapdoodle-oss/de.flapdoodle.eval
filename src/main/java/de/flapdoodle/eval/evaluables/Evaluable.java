package de.flapdoodle.eval.evaluables;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.util.List;

public interface Evaluable<T extends Value<?>> {
    T evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, List<? extends Value<?>> arguments) throws EvaluationException;

    default Evaluable<T> named(String name) {
        Evaluable<T> that = this;
        return new Evaluable<T>() {
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
