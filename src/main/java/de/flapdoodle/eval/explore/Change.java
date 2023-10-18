package de.flapdoodle.eval.explore;

import org.immutables.value.Value;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Function;

@Value.Immutable
public abstract class Change {
	public abstract TimeRange range();
	public abstract Function<LocalDateTime, LocalDateTime> increment();
	public abstract Calculation change();

	@Value.Auxiliary
	public boolean isActive(LocalDateTime currentTime) {
		return range().matches(currentTime);
	}

	@Value.Auxiliary
	public Optional<Integer> iteration(LocalDateTime currentTime) {
		if (range().matches(currentTime)) {
			int iteration;
			LocalDateTime interationDate = range().start();


//		long diff = ChronoUnit.DAYS.between(range().start(), currentTime);
//		increment().toDays();
		}
		return Optional.empty();
	}

	protected static boolean isSameDay(LocalDateTime a, LocalDateTime b) {
		return a.withHour(0).withMinute(0).withSecond(0).withNano(0)
			.isEqual(b.withHour(0).withMinute(0).withSecond(0).withNano(0));
	}

	public static ImmutableChange.Builder builder() {
		return ImmutableChange.builder();
	}
}
