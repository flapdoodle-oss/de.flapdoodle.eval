package de.flapdoodle.eval.evaluables;

import java.util.Optional;

public interface TypedEvaluableByNumberOfArguments {
    Optional<? extends TypedEvaluableByArguments> filterByNumberOfArguments(int numberOfArguments);
}
