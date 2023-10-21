package de.flapdoodle.eval.explore.calculation;

import org.immutables.value.Value;

@Value.Immutable
public interface FlowChange<T> {
	String name();
	T delta();

	static <T> FlowChange<T> of(String name, T delta) {
		return ImmutableFlowChange.<T>builder()
			.name(name)
			.delta(delta)
			.build();
	}
}
