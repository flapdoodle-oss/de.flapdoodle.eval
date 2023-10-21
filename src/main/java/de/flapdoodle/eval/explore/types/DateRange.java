package de.flapdoodle.eval.explore.types;

import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;

@Value.Immutable
public interface DateRange {
	LocalDate start();
	Optional<LocalDate> end();
	Function<LocalDate, LocalDate> next();

	static DateRange of(LocalDate start, Function<LocalDate, LocalDate> next) {
		return ImmutableDateRange.builder()
			.start(start)
			.next(next)
			.build();
	}

	static DateRange of(LocalDate start, LocalDate end, Function<LocalDate, LocalDate> next) {
		return ImmutableDateRange.builder()
			.start(start)
			.next(next)
			.end(end)
			.build();
	}
}
