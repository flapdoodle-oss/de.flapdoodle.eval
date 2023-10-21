package de.flapdoodle.eval.explore.model;

import de.flapdoodle.eval.explore.data.Change;
import de.flapdoodle.eval.explore.data.ChangeList;
import org.immutables.value.Value;

import java.util.function.BiFunction;

@Value.Immutable
public interface FlowFactory<T> {
	FlowId<T> id();
	T start();
	BiFunction<T, ChangeList<T>, T> aggregator();

	static <T> FlowFactory<T> of(FlowId<T> id, T start, BiFunction<T, ChangeList<T>, T> aggregator) {
		return ImmutableFlowFactory.<T>builder()
			.id(id)
			.start(start)
			.aggregator(aggregator)
			.build();
	}

	static FlowFactory<Double> ofDouble(FlowId<Double> id, double start) {
		return of(id, start, (s,l) -> s + l.changes().stream().mapToDouble(Change::delta).sum());
	}
}
