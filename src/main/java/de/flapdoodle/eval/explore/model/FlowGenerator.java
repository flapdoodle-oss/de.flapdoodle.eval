package de.flapdoodle.eval.explore.model;

import de.flapdoodle.eval.explore.types.FlowId;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class FlowGenerator {
	protected abstract List<FlowFactory<?>> flowFactories();
	protected abstract List<Changemaker<?,?>> changemakers();

	public void generate(LocalDate start, LocalDate end) {
		Map<FlowId<?>, FlowFactory<?>> factoryById = flowFactories().stream()
			.collect(Collectors.toMap(FlowFactory::id, Function.identity()));
		Map<FlowId<?>, Object> flowValues=flowFactories().stream()
			.collect(Collectors.toMap(FlowFactory::id, FlowFactory::start));

		LocalDate current=start;

		for (Changemaker<?, ?> changemaker : changemakers()) {
			// track date for changemaker
		}
	}

	public static ImmutableFlowGenerator.Builder builder() {
		return ImmutableFlowGenerator.builder();
	}
}
