package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.parser.Token;

@org.immutables.value.Value.Immutable
public abstract class AnyTypeValueNode<T> extends ValueNode<T> {
	@org.immutables.value.Value.Parameter
	protected abstract Value<T> value();

	@Override
	public Value<T> evaluate(ValueResolver variableResolver, EvaluationContext context) {
		return value();
	}

	public static <T> AnyTypeValueNode<T> of(Token token, Value<T> value) {
		return ImmutableAnyTypeValueNode.<T>builder()
			.token(token)
			.value(value)
			.build();
	}
}
