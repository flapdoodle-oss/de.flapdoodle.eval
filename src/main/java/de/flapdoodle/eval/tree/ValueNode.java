package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.VariableResolver;
import de.flapdoodle.eval.parser.Token;

@org.immutables.value.Value.Immutable
public abstract class ValueNode<T> extends Node {
	@org.immutables.value.Value.Parameter
	protected abstract T value();

	@Override
	public T evaluate(VariableResolver variableResolver, EvaluationContext context) {
		return value();
	}

	public static <T> ValueNode<T> of(Token token, T value) {
		return ImmutableValueNode.<T>builder()
			.token(token)
			.value(value)
			.build();
	}
}
