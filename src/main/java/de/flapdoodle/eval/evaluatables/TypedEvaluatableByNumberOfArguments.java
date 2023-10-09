package de.flapdoodle.eval.evaluatables;

import java.util.Optional;

public interface TypedEvaluatableByNumberOfArguments {
    Optional<? extends TypedEvaluatableByArguments> filterByNumberOfArguments(int numberOfArguments);
}
