package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

public abstract class Node {
	@org.immutables.value.Value.Parameter
	protected abstract Token token();

	@org.immutables.value.Value.Auxiliary
	public abstract Value<?> evaluate(ValueResolver variableResolver, EvaluationContext context) throws EvaluationException;
}
