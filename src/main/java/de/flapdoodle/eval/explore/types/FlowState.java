package de.flapdoodle.eval.explore.types;

import org.immutables.value.Value;

@Value.Immutable
public interface FlowState<T> {
	T before();
	T after();

	static <T> FlowState<T> of(T before, T after) {
		return ImmutableFlowState.<T>builder()
			.before(before)
			.after(after)
			.build();
	}
}
