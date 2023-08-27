package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.PrefixOperator;

public class PrefixOperatorNode extends Node {
	@Override
	public Value<?> evaluate(ValueResolver variableResolver) {
		return null;
	}

	public static PrefixOperatorNode of(PrefixOperator operator, Node parameter) {
		return null;
	}
}
