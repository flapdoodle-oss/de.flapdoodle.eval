package de.flapdoodle.eval.core.evaluables;

import java.util.Optional;

public interface TypedEvaluableByName {
    Optional<? extends TypedEvaluableByArguments> find(String name, int numberOfArguments);

    default TypedEvaluableByName andThen(TypedEvaluableByName fallback) {
        TypedEvaluableByName that=this;
        return (name, numberOfArguments) -> {
            Optional<? extends TypedEvaluableByArguments> ret = that.find(name, numberOfArguments);
            return ret.isPresent()
                    ? ret
                    : fallback.find(name, numberOfArguments);
        };
    }
}
