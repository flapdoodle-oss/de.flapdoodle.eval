package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.exceptions.EvaluationException;
import org.immutables.value.Value;

@Value.Immutable
public abstract class EvalFailedWithException {
	public abstract EvaluationException exception();

	public static EvalFailedWithException of(EvaluationException exception) {
		return ImmutableEvalFailedWithException.builder()
			.exception(exception)
			.build();
	}
}
