package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.exceptions.EvaluationException;

import java.util.Optional;

public interface EvaluableExceptionMapper {
	Object map(EvaluationException ex);
	Optional<EvaluationException> match(Object value);
}
