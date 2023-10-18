package de.flapdoodle.eval.explore;

import de.flapdoodle.checks.Preconditions;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Value.Immutable
public abstract class Cashflow {
	public abstract List<Change> changes();

	public void calculate(LocalDateTime start, LocalDateTime end) {
		Preconditions.checkArgument(start.isBefore(end),"%s >= %s", start, end);

		LocalDateTime currentTime=start;
		double currentValue=0;

		for (Change change : changes()) {
			if (change.isActive(currentTime)) {
				Optional<Integer> iteration = change.iteration(currentTime);
			}
		}
	}

	public static ImmutableCashflow.Builder builder() {
		return ImmutableCashflow.builder();
	}

}
