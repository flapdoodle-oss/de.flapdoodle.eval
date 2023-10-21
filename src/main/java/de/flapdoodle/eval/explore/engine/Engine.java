package de.flapdoodle.eval.explore.engine;

import de.flapdoodle.eval.explore.calculation.Transaction;
import de.flapdoodle.eval.explore.types.Flow;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.List;

@Value.Immutable
public abstract class Engine {
	protected abstract List<Flow<?>> flows();
	protected abstract List<Transaction> transactions();

	protected void calculate(LocalDate start, LocalDate end) {

		FlowStates states = FlowStates.empty()
			.with(start, getStartStates());
	}

	private FlowStateMap getStartStates() {
		FlowStateMap startStates=FlowStateMap.empty();
		for (Flow flow : flows()) {
			startStates=startStates.with(flow.id(), flow.start());
		}
		return startStates;
	}

	public static ImmutableEngine.Builder builder() {
		return ImmutableEngine.builder();
	}
}
