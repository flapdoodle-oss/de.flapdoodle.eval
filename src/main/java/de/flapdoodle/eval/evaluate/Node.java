package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Node {
	@org.immutables.value.Value.Parameter
	protected abstract Token token();

	@org.immutables.value.Value.Auxiliary
	public abstract Value<?> evaluate(ValueResolver variableResolver, EvaluationContext context) throws EvaluationException;

	public static Set<String> usedVariables(Node root) {
		Set<String> ret = new LinkedHashSet<>();
		
		return Collections.unmodifiableSet(ret);
	}
}
