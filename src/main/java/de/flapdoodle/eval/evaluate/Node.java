package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;

public abstract class Node {

	public abstract Value<?> evaluate(ValueResolver variableResolver);
}
