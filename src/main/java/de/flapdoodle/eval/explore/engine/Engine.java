package de.flapdoodle.eval.explore.engine;

import de.flapdoodle.checks.Preconditions;
import de.flapdoodle.eval.explore.calculation.Calculation;
import de.flapdoodle.eval.explore.calculation.FlowChange;
import de.flapdoodle.eval.explore.calculation.FlowStateLookup;
import de.flapdoodle.eval.explore.calculation.Transaction;
import de.flapdoodle.eval.explore.types.Flow;
import de.flapdoodle.eval.explore.types.FlowId;
import de.flapdoodle.eval.explore.types.FlowState;
import org.immutables.value.Value;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class Engine {
	protected abstract List<Flow<?>> flows();
	protected abstract List<Transaction> transactions();

	protected void calculate(LocalDate start, LocalDate end) {
		Preconditions.checkArgument(start.isBefore(end),"%s >= %s", start, end);

		Map<FlowId<?>, Flow<?>> flowMap = flows().stream()
			.collect(Collectors.toMap(Flow::id, Function.identity()));

		FlowStates states = FlowStates.empty()
			.with(start.minusDays(1), getStartStates());

		Map<Transaction, LocalDate> lastRun=new LinkedHashMap<>();
		LocalDate current=start;

		do {
			System.out.println("--> "+current);

			List<FlowChangeEntry<?>> changes=new ArrayList<>();

			for (Transaction transaction : transactions()) {
				if (transaction.section().isActive(current)) {
					LocalDate transactionLastRun = Optional.ofNullable(lastRun.get(transaction))
						.orElse(current.minusDays(1));
					Duration duration = Duration.ofDays(ChronoUnit.DAYS.between(transactionLastRun, current));
					FlowStateLookup flowStateLookup = states.stateLookupOf(transactionLastRun, current);

					for (Calculation<?> calculation : transaction.calculations()) {
						FlowChangeEntry<?> entry = calculate(calculation, flowStateLookup, duration);
						changes.add(entry);
					}
				}
			}

			states = newStateMap(current, states, changes);

			current=current.plusDays(1);
		} while (!current.isAfter(end));
	}

	private FlowStateMap getStartStates() {
		FlowStateMap startStates=FlowStateMap.empty();
		for (Flow flow : flows()) {
			startStates=startStates.with(flow.id(), FlowState.of(flow.start(), flow.start()));
		}
		return startStates;
	}

	private static <T> FlowChangeEntry<T> calculate(Calculation<T> calculation, FlowStateLookup lookup, Duration duration) {
		FlowChange<T> change = calculation.evaluate(lookup, duration);
		return new FlowChangeEntry<>(calculation.destination(), change);
	}

	private static FlowStates newStateMap(LocalDate current, FlowStates states, List<FlowChangeEntry<?>> changes) {
		FlowStateMap lastStateMap = states.get(current.minusDays(1));
		FlowStateMap newStateMap = aggregate(lastStateMap, changes);

		return states.with(current, newStateMap);
	}

	private static FlowStateMap aggregate(FlowStateMap lastStateMap, List<FlowChangeEntry<?>> changes) {
		FlowStateMap newMap = FlowStateMap.empty();
		Set<FlowId<?>> flowIds = changes.stream().map(FlowChangeEntry::destination).collect(Collectors.toSet());
		for (FlowId<?> flowId : flowIds) {
			newMap = aggregate(newMap, flowId, lastStateMap, changes);
		}
		return newMap;
	}

	private static <T> FlowStateMap aggregate(FlowStateMap newMap, FlowId<T> flowId, FlowStateMap lastStateMap, List<FlowChangeEntry<?>> changes) {
		FlowState<T> lastState = Preconditions.checkPresent(lastStateMap.get(flowId),"last state not found: %s", flowId).get();
		T lastValue=lastState.after();

		List<FlowChangeEntry<T>> filteredChanges = changes.stream().filter(it -> it.destination().equals(flowId))
			.map(it -> (FlowChangeEntry<T>) it)
			.collect(Collectors.toList());

		T newValue = filteredChanges.stream()
			.map(it -> it.change().delta())
			.reduce((t, t2) -> flowId.type().reduce().apply(t, t2))
			.map(it -> flowId.type().reduce().apply(it, lastValue))
			.orElse(lastValue);

		return newMap.with(flowId, FlowState.of(lastValue, newValue));
	}

	private static class FlowChangeEntry<T> {
		private final FlowId<T> destination;
		private final FlowChange<T> change;
		public FlowChangeEntry(FlowId<T> destination, FlowChange<T> change) {
			this.destination = destination;
			this.change = change;
		}

		public FlowId<T> destination() {
			return destination;
		}

		public FlowChange<T> change() {
			return change;
		}
	}

	public static ImmutableEngine.Builder builder() {
		return ImmutableEngine.builder();
	}
}
