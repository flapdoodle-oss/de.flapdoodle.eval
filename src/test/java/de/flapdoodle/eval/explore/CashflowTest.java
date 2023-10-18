package de.flapdoodle.eval.explore;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class CashflowTest {

	@Test
	public void rateUberZeitraum() {
		LocalDateTime now = LocalDateTime.of(2023,8,13,0,0,0);

		Cashflow cashFlow = Cashflow.builder()
			.addChanges(Change.builder()
				.change(((current, iteration) -> current+10.0))
				.increment(current -> current.plusDays(5))
				.range(TimeRange.of(now))
				.build())
			.build();

		cashFlow.calculate(now, now.plusDays(30));
	}
}