package de.flapdoodle.eval.core.evaluables;

import java.util.Optional;

public interface TypedEvaluableByNumberOfArguments {
    Optional<? extends TypedEvaluableByArguments> filterByNumberOfArguments(int numberOfArguments);
}
