package de.flapdoodle.eval.explore.calculation;

import de.flapdoodle.eval.explore.types.FlowId;
import de.flapdoodle.eval.explore.types.FlowState;
import org.immutables.value.Value;

import java.time.Duration;

public interface Calculation<T> {
	FlowId<T> destination();
	FlowChange<T> evaluate(FlowStateLookup flowStateLookup, Duration duration);

	interface Arg0<T> {
		FlowChange<T> evaluate(Duration duration);
	}

	interface Arg1<S, T> {
		FlowChange<T> evaluate(FlowState<S> source, Duration duration);
	}

	@Value.Immutable
	abstract class Generator<T> implements Calculation<T> {
		protected abstract Arg0<T> transformation();

		@Override
		public FlowChange<T> evaluate(FlowStateLookup flowStateLookup, Duration duration) {
			return transformation().evaluate(duration);
		}
	}

	@Value.Immutable
	abstract class Single<S, T> implements Calculation<T> {
		protected abstract FlowId<S> source();
		protected abstract Arg1<S, T> transformation();

		@Override
		public FlowChange<T> evaluate(FlowStateLookup flowStateLookup, Duration duration) {
			FlowState<S> s = flowStateLookup.stateOf(source());
			return transformation().evaluate(s, duration);
		}
	}

	static <T> Generator<T> of(FlowId<T> dest, Arg0<T> transformation) {
		return ImmutableGenerator.<T>builder()
			.destination(dest)
			.transformation(transformation)
			.build();
	}

	static <S, T> Single<S, T> of(FlowId<T> dest, FlowId<S> source, Arg1<S, T> transformation) {
		return ImmutableSingle.<S, T>builder()
			.destination(dest)
			.source(source)
			.transformation(transformation)
			.build();
	}
}
