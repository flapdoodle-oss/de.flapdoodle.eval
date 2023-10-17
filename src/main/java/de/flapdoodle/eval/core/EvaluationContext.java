package de.flapdoodle.eval.core;

import java.math.MathContext;
import java.time.ZoneId;

@org.immutables.value.Value.Immutable
public interface EvaluationContext {
	MathContext mathContext();

	ZoneId zoneId();

	static ImmutableEvaluationContext.Builder builder() {
		return ImmutableEvaluationContext.builder();
	}
}
