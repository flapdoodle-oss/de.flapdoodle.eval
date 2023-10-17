package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

@org.immutables.value.Value.Immutable
public abstract class AnyTypeValueNode<T> extends ValueNode<T> {
	@org.immutables.value.Value.Parameter
	protected abstract T value();

	@Override
	public T evaluate(ValueResolver variableResolver, EvaluationContext context) {
		return value();
	}

	public static <T> AnyTypeValueNode<T> of(Token token, T value) {
		return ImmutableAnyTypeValueNode.<T>builder()
			.token(token)
			.value(value)
			.build();
	}
}
