package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;

public class ArrayAccessNode extends Node {
	@Override
	public Value<?> evaluate(ValueResolver variableResolver) {
		return null;
	}

	public static ArrayAccessNode of(Node array, Node index) {
		return null;
	}
}
