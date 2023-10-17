package de.flapdoodle.eval;

import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.ParseException;
import de.flapdoodle.eval.tree.Node;

import java.math.MathContext;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@org.immutables.value.Value.Immutable
public abstract class Expression {
	protected abstract MathContext mathContext();
	protected abstract ZoneId zoneId();
	protected abstract Node root();

	@org.immutables.value.Value.Auxiliary
	public Object evaluate(VariableResolver variableResolver) throws EvaluationException, ParseException {
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
	public Set<String> undefinedVariables(VariableResolver variableResolver) {
		return usedVariables().stream()
			.filter(name -> !variableResolver.has(name))
			.collect(Collectors.toSet());
	}

	@org.immutables.value.Value.Auxiliary
	public List<Node> allNodes() {
		return Node.allNodes(root());
	}

	public static ImmutableExpression.Builder builder() {
		return ImmutableExpression.builder();
	}

}
