package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.VariableResolver;
import de.flapdoodle.eval.parser.Token;

@org.immutables.value.Value.Immutable
public abstract class ComparableValueNode<T extends Comparable<T>> extends ValueNode<T> {
	@org.immutables.value.Value.Parameter
	protected abstract T value();

	@Override
	public T evaluate(VariableResolver variableResolver, EvaluationContext context) {
		return value();
	}

	public static <T extends Comparable<T>> ComparableValueNode<T> of(Token token, T value) {
		return ImmutableComparableValueNode.<T>builder()
			.token(token)
			.value(value)
			.build();
	}
}
