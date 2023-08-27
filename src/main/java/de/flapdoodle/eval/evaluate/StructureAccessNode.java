package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;

public class StructureAccessNode extends Node {
	@Override
	public Value<?> evaluate(ValueResolver variableResolver) {
		return null;
	}

	public static StructureAccessNode of(Node structure, String name) {
		return null;
	}
}
