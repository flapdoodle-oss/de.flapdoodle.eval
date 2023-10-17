package de.flapdoodle.eval.core.evaluables;

import de.flapdoodle.eval.core.exceptions.EvaluableException;
import de.flapdoodle.types.Either;

import java.util.List;

public interface TypedEvaluableByArguments {
    Either<TypedEvaluable<?>, List<EvaluableException>> find(List<?> values);
}
