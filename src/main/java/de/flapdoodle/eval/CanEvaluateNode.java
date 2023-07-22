package de.flapdoodle.eval;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.ASTNode;

public interface CanEvaluateNode {
	Value<?> evaluateSubtree(ValueResolver variableResolver, ASTNode startNode) throws EvaluationException;
}
