package de.flapdoodle.eval;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.ASTNode;
import de.flapdoodle.types.ThrowingFunction;

import java.math.MathContext;
import java.time.ZoneId;

@org.immutables.value.Value.Immutable
public interface EvaluationContext {
	MathContext mathContext();

	ZoneId zoneId();

//	Value<?> evaluateSubtree(ValueResolver variableResolver, ASTNode startNode) throws EvaluationException;

	ThrowingFunction<ASTNode, Value<?>, EvaluationException> subtreeEvaluator();

	static ImmutableEvaluationContext.Builder builder() {
		return ImmutableEvaluationContext.builder();
	}
}
