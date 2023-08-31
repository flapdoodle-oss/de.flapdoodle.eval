package de.flapdoodle.eval;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.tree.Node;
import de.flapdoodle.eval.parser.ParseException;

import java.math.MathContext;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@org.immutables.value.Value.Immutable
public abstract class ParsedExpression {
	protected abstract MathContext mathContext();
	protected abstract ZoneId zoneId();
	protected abstract Node root();

	@org.immutables.value.Value.Auxiliary
	public Value<?> evaluate(ValueResolver variableResolver) throws EvaluationException, ParseException {
		return root().evaluate(variableResolver, EvaluationContext.builder()
			.mathContext(mathContext())
			.zoneId(zoneId())
			.build());
	}

	@org.immutables.value.Value.Derived
	public Set<String> usedVariables() {
		return Node.usedVariables(root());
	}

	@org.immutables.value.Value.Auxiliary
	public List<Node> allNodes() {
		return Node.allNodes(root());
	}

	public static ImmutableParsedExpression.Builder builder() {
		return ImmutableParsedExpression.builder();
	}
}
