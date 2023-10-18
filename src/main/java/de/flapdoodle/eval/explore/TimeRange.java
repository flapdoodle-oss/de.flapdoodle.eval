package de.flapdoodle.eval.explore;

import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.Optional;

@Value.Immutable
public interface TimeRange {
	LocalDateTime start();
	Optional<LocalDateTime> end();

	@Value.Auxiliary
	default boolean matches(LocalDateTime currentTime) {
		boolean startIsBeforeOrEqual = !start().isAfter(currentTime);
		boolean endIsAfterOrNotLimited = !end().isPresent() || end().get().isAfter(currentTime);
		return startIsBeforeOrEqual && endIsAfterOrNotLimited;
	}

	static TimeRange of(LocalDateTime start) {
		return ImmutableTimeRange.builder()
			.start(start)
			.build();
	}

	static TimeRange of(LocalDateTime start, LocalDateTime end) {
		return ImmutableTimeRange.builder()
			.start(start)
			.end(end)
			.build();
	}

}
