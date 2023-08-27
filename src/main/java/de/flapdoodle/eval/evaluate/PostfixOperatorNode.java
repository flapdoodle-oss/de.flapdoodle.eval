package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.PostfixOperator;

public abstract class PostfixOperatorNode extends Node {
	protected abstract PostfixOperator operator();
	protected abstract Node parameter();
	
	@Override
	public Value<?> evaluate(ValueResolver variableResolver) {
		return null; //operator().evaluate(variableResolver);
	}

	public static PostfixOperatorNode of(PostfixOperator operator, Node parameter) {
		return null;
	}
}