package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.ASTNode;

public class LazyNode extends Node {

	@Override
	public Value<?> evaluate(ValueResolver variableResolver) {
		return null;
	}

	public static LazyNode of(Node node) {
		return null;
	}
}
