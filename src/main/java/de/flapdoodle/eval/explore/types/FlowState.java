package de.flapdoodle.eval.explore.types;

import org.immutables.value.Value;

@Value.Immutable
public interface FlowState<T> {
	T current();
	T last();

	static <T> FlowState<T> of(T last, T current) {
		return ImmutableFlowState.<T>builder()
			.last(last)
			.current(current)
			.build();
	}
}
