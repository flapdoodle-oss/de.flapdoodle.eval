package de.flapdoodle.eval.explore.engine;

import de.flapdoodle.eval.explore.types.FlowId;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
public abstract class FlowStateMap {
	protected abstract Map<FlowId<?>, Object> map();

	@Value.Auxiliary
	public <T> Optional<T> get(FlowId<T> id) {
		return Optional.ofNullable(id.type().cast(map().get(id)));
	}

	public <T> FlowStateMap with(FlowId<T> id, T value) {
		return ImmutableFlowStateMap.builder().from(this)
			.putMap(id, value)
			.build();
	}

	public static FlowStateMap empty() {
		return ImmutableFlowStateMap.builder().build();
	}
}
