package de.flapdoodle.eval.evaluables;

import de.flapdoodle.eval.exceptions.EvaluableException;
import de.flapdoodle.types.Either;

import java.util.List;

public interface TypedEvaluableByArguments {
    Either<TypedEvaluable<?>, List<EvaluableException>> find(List<?> values);
}
