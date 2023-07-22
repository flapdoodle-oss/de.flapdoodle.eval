package de.flapdoodle.eval.config;

import de.flapdoodle.eval.Evaluateable;

public interface EvaluateableResolver {
	Evaluateable get(String name);

	default boolean has(String name) {
		return get(name) != null;
	}

	default EvaluateableResolver andThen(EvaluateableResolver fallback) {
		EvaluateableResolver that=this;

		return name -> {
			Evaluateable function = that.get(name);
			if (function==null) {
				return fallback.get(name);
			}
			return function;
		};
	}
}
