package de.flapdoodle.eval.explore.engine;

import de.flapdoodle.eval.explore.calculation.Calculation;
import de.flapdoodle.eval.explore.calculation.FlowChange;
import de.flapdoodle.eval.explore.calculation.Transaction;
import de.flapdoodle.eval.explore.types.DateRange;
import de.flapdoodle.eval.explore.types.Flow;
import de.flapdoodle.eval.explore.types.FlowId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class EngineTest {

	@Test
	void sample() {
		LocalDate now=LocalDate.of(2012,3,24);

		FlowId<Double> dummy = FlowId.of(Double.class);

		DateRange eachDay = DateRange.of(now, it -> it.plusDays(1));

		Engine engine = Engine.builder()
			.addFlows(Flow.of(dummy, 0d, Double::sum))
			.addTransactions(Transaction.builder()
				.section(eachDay)
				.addCalculations(Calculation.of(dummy, duration -> FlowChange.of("add", 10.0d)))
				.build())
			.build();

		engine.calculate(now, now.plusDays(10));
	}
}