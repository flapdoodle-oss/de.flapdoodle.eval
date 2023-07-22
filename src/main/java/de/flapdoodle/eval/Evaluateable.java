package de.flapdoodle.eval;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.util.List;

public interface Evaluateable {
	Parameters parameters();

	Value<?> evaluate(ValueResolver valueResolver, Expression expression, Token token, List<Value<?>> arguments) throws EvaluationException;

	default boolean parameterIsLazy(int parameterIndex) {
		return parameters().isLazy(parameterIndex);
	}

}
