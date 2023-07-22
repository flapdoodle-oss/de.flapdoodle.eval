package de.flapdoodle.eval;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.ASTNode;

import java.math.MathContext;
import java.time.ZoneId;

public interface EvaluationContext {
	@org.immutables.value.Value.Default
	MathContext mathContext();

	@org.immutables.value.Value.Default
	ZoneId zoneId();

	Value<?> evaluateSubtree(ValueResolver variableResolver, ASTNode startNode) throws EvaluationException;
}
