package de.flapdoodle.eval.explore.types;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface FlowId<T> {
	UUID id();
	FlowType<T> type();

	static <T> FlowId<T> of(FlowType<T> type) {
		return ImmutableFlowId.<T>builder()
			.id(UUID.randomUUID())
			.type(type)
			.build();
	}
}
