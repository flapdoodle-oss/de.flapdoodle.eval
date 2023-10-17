package de.flapdoodle.eval.core.tree;

import de.flapdoodle.eval.core.exceptions.EvaluationException;

import java.util.Optional;

public interface EvaluableExceptionMapper {
	Object map(EvaluationException ex);
	Optional<EvaluationException> match(Object value);
}
