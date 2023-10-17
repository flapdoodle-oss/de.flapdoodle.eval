package de.flapdoodle.eval.core.evaluables;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;

import java.util.List;

public interface Evaluable<T> {
    T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, List<?> arguments) throws EvaluationException;

    default Evaluable<T> named(String name) {
        Evaluable<T> that = this;
        return new Evaluable<T>() {
            @Override
            public T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, List<?> arguments)
              throws EvaluationException {
                return that.evaluate(variableResolver,evaluationContext,token,arguments);
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }
}
