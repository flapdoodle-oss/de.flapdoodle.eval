package de.flapdoodle.eval.explore.types;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface FlowId<T> {
	UUID id();
	Class<T> type();

	static <T> FlowId<T> of(Class<T> type) {
		return ImmutableFlowId.<T>builder()
			.id(UUID.randomUUID())
			.type(type)
			.build();
	}
}
