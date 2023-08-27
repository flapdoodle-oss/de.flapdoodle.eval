package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;

@org.immutables.value.Value.Immutable
public abstract class ComparableValueNode<T extends Comparable<T>> extends ValueNode<T> {
	protected abstract Value.ComparableValue<T> value();

	@Override
	public Value<?> evaluate(ValueResolver variableResolver) {
		return value();
	}

	public static <T extends Comparable<T>> ComparableValueNode<T> of(Value.ComparableValue<T> value) {
		return ImmutableComparableValueNode.<T>builder()
			.value(value)
			.build();
	}
}
