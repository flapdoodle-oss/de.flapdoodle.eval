package de.flapdoodle.eval.explore.calculation;

import de.flapdoodle.eval.explore.types.DateRange;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface Transaction {
	DateRange section();
	List<Calculation<?>> calculations();

	static ImmutableTransaction.Builder builder() {
		return ImmutableTransaction.builder();
	}
}
