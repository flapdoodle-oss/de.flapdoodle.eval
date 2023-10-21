package de.flapdoodle.eval.explore.model;

import java.time.Duration;
import java.time.LocalDate;

public interface Processor<S, D> {
	Transaction<S, D> process(LocalDate date, S source, D destination, Duration duration);
}
