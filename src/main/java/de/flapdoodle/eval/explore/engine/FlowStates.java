package de.flapdoodle.eval.explore.engine;

import de.flapdoodle.checks.Preconditions;
import de.flapdoodle.eval.explore.calculation.FlowStateLookup;
import de.flapdoodle.eval.explore.types.FlowId;
import de.flapdoodle.eval.explore.types.FlowState;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Value.Immutable
public abstract class FlowStates {
	protected abstract Map<LocalDate, FlowStateMap> values();

	@Value.Auxiliary
	public FlowStateLookup of(LocalDate lastDate, LocalDate date) {
		return new FlowStateLookup() {
			@Override
			public <T> FlowState<T> stateOf(FlowId<T> id) {
				FlowStateMap current = Preconditions.checkNotNull(values().get(date), "could not get state for %s", date);
				FlowStateMap last = values().get(lastDate);
				Optional<T> currentValue = current.get(id);
				Preconditions.checkArgument(currentValue.isPresent(), "could not find state for %s", id);
				return FlowState.of(last.get(id).orElse(currentValue.get()), currentValue.get());
			}
		};
	}

	public FlowStates with(LocalDate date, FlowStateMap states) {
		return ImmutableFlowStates.builder().from(this)
			.putValues(date, states)
			.build();
	}

	public static FlowStates empty() {
		return ImmutableFlowStates.builder()
			.build();
	}
}
