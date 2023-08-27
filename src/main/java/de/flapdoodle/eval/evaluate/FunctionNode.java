package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.Evaluateable;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;

import java.util.List;

public class FunctionNode extends Node {
	@Override
	public Value<?> evaluate(ValueResolver variableResolver) {
		return null;
	}
	
	public static FunctionNode of(Evaluateable function, List<Node> parameterResults) {
		return null;
	}
}
