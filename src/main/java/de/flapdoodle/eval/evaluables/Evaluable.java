package de.flapdoodle.eval.evaluables;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.VariableResolver;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.Token;

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
