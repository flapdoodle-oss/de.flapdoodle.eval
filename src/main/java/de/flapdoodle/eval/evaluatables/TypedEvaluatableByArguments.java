package de.flapdoodle.eval.evaluatables;

import de.flapdoodle.eval.EvaluatableException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.types.Either;

import java.util.List;

public interface TypedEvaluatableByArguments {
    Either<TypedEvaluatable<?>, List<EvaluatableException>> find(List<? extends Value<?>> values);
}
