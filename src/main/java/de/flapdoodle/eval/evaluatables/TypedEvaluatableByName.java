package de.flapdoodle.eval.evaluatables;

import java.util.Optional;

public interface TypedEvaluatableByName {
    Optional<? extends TypedEvaluatableByArguments> find(String name, int numberOfArguments);

    default TypedEvaluatableByName andThen(TypedEvaluatableByName fallback) {
        TypedEvaluatableByName that=this;
        return (name, numberOfArguments) -> {
            Optional<? extends TypedEvaluatableByArguments> ret = that.find(name, numberOfArguments);
            return ret.isPresent()
                    ? ret
                    : fallback.find(name, numberOfArguments);
        };
    }
}
