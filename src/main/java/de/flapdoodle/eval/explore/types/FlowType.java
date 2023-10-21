package de.flapdoodle.eval.explore.types;

import org.immutables.value.Value;

import java.util.function.BiFunction;

@Value.Immutable
public abstract class FlowType<T> {
	@Value.Parameter
	public abstract Class<T> type();
	@Value.Parameter
	public abstract BiFunction<T, T, T> reduce();

	public static final FlowType<Double> DOUBLE=of(Double.class, Double::sum);

	public static <T> FlowType<T> of(Class<T> type, BiFunction<T, T, T> reduce) {
		return ImmutableFlowType.of(type, reduce);
	}
}
