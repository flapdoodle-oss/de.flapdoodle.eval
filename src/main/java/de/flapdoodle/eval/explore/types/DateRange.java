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

	@Value.Auxiliary
	default boolean isActive(LocalDate current) {
		return isBeforeOrEqual(start(), current) && (!end().isPresent() || isAfterOrEqual(end().get(), current));
	}

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

	static boolean isBeforeOrEqual(LocalDate a, LocalDate b) {
		return !a.isAfter(b);
	}

	static boolean isAfterOrEqual(LocalDate a, LocalDate b) {
		return !a.isBefore(b);
	}
}
