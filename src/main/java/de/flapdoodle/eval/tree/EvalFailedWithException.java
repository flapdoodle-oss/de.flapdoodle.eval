package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.exceptions.EvaluationException;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class EvalFailedWithException {
	public abstract EvaluationException exception();

	public static EvalFailedWithException of(EvaluationException exception) {
		return ImmutableEvalFailedWithException.builder()
			.exception(exception)
			.build();
	}

	public static EvaluableExceptionMapper mapper() {
		return new EvaluableExceptionMapper() {
			@Override
			public Object map(EvaluationException ex) {
				return EvalFailedWithException.of(ex);
			}
			@Override
			public Optional<EvaluationException> match(Object value) {
				return value instanceof EvalFailedWithException
					? Optional.of(((EvalFailedWithException) value).exception())
					: Optional.empty();
			}
		};
	}
}
