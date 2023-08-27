package de.flapdoodle.eval;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;

import java.util.List;

public interface Evaluateable {
	Parameters parameters();

	Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, CommonToken token, List<Value<?>> arguments) throws EvaluationException;

	default boolean parameterIsLazy(int parameterIndex) {
		return parameters().isLazy(parameterIndex);
	}

}
