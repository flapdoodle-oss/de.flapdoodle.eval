package de.flapdoodle.eval.core.tree;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.core.exceptions.EvaluationException;

@org.immutables.value.Value.Immutable
public abstract class LookupNode extends Node {

	@Override
	public Object evaluate(VariableResolver variableResolver, EvaluationContext context) throws EvaluationException {
		Object result = variableResolver.get(token().value());
		if (result == null) {
			throw new EvaluationException(
				token(), String.format("Variable or constant value for '%s' not found", token().value()));
		}
		return result;
	}

	public static LookupNode of(Token token) {
		return ImmutableLookupNode.builder()
			.token(token)
			.build();
	}
}
