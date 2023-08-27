package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

@org.immutables.value.Value.Immutable
public abstract class ComparableValueNode<T extends Comparable<T>> extends ValueNode<T> {
	@org.immutables.value.Value.Parameter
	protected abstract Value.ComparableValue<T> value();

	@Override
	public Value.ComparableValue<T> evaluate(ValueResolver variableResolver, EvaluationContext context) {
		return value();
	}

	public static <T extends Comparable<T>> ComparableValueNode<T> of(Token token, Value.ComparableValue<T> value) {
		return ImmutableComparableValueNode.<T>builder()
			.token(token)
			.value(value)
			.build();
	}
}