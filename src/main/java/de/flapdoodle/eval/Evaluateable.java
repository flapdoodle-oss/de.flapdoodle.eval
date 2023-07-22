package de.flapdoodle.eval;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.util.List;

public interface Evaluateable {
	Value<?> evaluate(ValueResolver valueResolver, Expression expression, Token token, List<Value<?>> parameters) throws EvaluationException;

	Parameters parameters();
	
	default int minArgs() {
		return parameters().min();
	}

	default int maxArgs() {
		return parameters().max();
	}

	default boolean parameterIsLazy(int parameterIndex) {
		return parameters().isLazy(parameterIndex);
	}

}
