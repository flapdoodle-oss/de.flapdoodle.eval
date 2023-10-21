package de.flapdoodle.eval.explore.model;

import de.flapdoodle.eval.explore.data.Change;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;

class FlowGeneratorTest {
	@Test
	void sample() {
		LocalDate now=LocalDate.of(2012,3,24);
		FlowId<Double> dummy = FlowId.of(Double.class);

		Processor<Double, Double> processor= (date, source, destination, duration) -> Transaction.of(
			Change.of("-10", date, -10.0),
			Change.of("-10", date, 10.0)
		);

		DateRange eachDay = DateRange.of(now, it -> it.plusDays(1));

		ImmutableFlowGenerator generator = FlowGenerator.builder()
			.addFlowFactories(FlowFactory.ofDouble(dummy, 0))
			.addChangemakers(Changemaker.of(dummy,dummy, eachDay, processor))
			.build();

		
		
	}
}