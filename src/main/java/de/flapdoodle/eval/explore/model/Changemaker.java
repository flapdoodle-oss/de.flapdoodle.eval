package de.flapdoodle.eval.explore.model;

import org.immutables.value.Value;

@Value.Immutable
public interface Changemaker<S, D> {
	FlowId<S> source();
	FlowId<D> destination();
	DateRange range();
	Processor<S, D> processor();

	static <S,D> ImmutableChangemaker.Builder<S, D> builder() {
		return ImmutableChangemaker.builder();
	}

	static <S,D> Changemaker<S,D> of(FlowId<S> source, FlowId<D> dest, DateRange range, Processor<S, D> processor) {
		return ImmutableChangemaker.<S,D>builder()
			.source(source)
			.destination(dest)
			.range(range)
			.processor(processor)
			.build();
	}
}
