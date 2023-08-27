package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.InfixOperator;

public class InfixOperatorNode extends Node {
	@Override
	public Value<?> evaluate(ValueResolver variableResolver) {
		return null;
	}

	public static Node of(InfixOperator operator, Node left, Node right) {
		return null;
	}
}
