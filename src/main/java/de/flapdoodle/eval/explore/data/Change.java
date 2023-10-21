package de.flapdoodle.eval.explore.data;

import de.flapdoodle.eval.explore.model.Changemaker;
import org.immutables.value.Value;

import java.time.LocalDate;

@Value.Immutable
public interface Change<T> {
	@Value.Parameter
	String name();
	@Value.Parameter
	LocalDate time();
	@Value.Parameter
	T delta();

	static <T> Change<T> of(String name, LocalDate date, T delta) {
		return ImmutableChange.of(name, date, delta);
	}
}
