package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.VariableResolver;

@org.immutables.value.Value.Immutable
public abstract class ValueLookup extends Node {

	@Override
	public Object evaluate(VariableResolver variableResolver, EvaluationContext context) throws EvaluationException {
		Object result = variableResolver.get(token().value());
		if (result == null) {
			throw new EvaluationException(
				token(), String.format("Variable or constant value for '%s' not found", token().value()));
		}
		return result;
	}

	public static ValueLookup of(Token token) {
		return ImmutableValueLookup.builder()
			.token(token)
			.build();
	}
}
