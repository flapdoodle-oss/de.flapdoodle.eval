package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

@org.immutables.value.Value.Immutable
public abstract class ValueLookup extends Node {

	@Override
	public Value<?> evaluate(ValueResolver variableResolver, EvaluationContext context) throws EvaluationException {
		Value<?> result = variableResolver.get(token().value());
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
