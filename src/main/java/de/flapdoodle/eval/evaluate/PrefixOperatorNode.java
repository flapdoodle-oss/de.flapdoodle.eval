package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.PrefixOperator;
import de.flapdoodle.eval.parser.Token;

@org.immutables.value.Value.Immutable
public abstract class PrefixOperatorNode extends Node {
	@org.immutables.value.Value.Parameter
	protected abstract PrefixOperator operator();
	@org.immutables.value.Value.Parameter
	protected abstract Node operand();

	@Override
	public Value<?> evaluate(ValueResolver variableResolver, EvaluationContext context) throws EvaluationException {
		return operator().evaluate(variableResolver,context, token(), operand().evaluate(variableResolver, context));
	}

	public static PrefixOperatorNode of(Token token, PrefixOperator operator, Node operand) {
		return ImmutablePrefixOperatorNode.builder()
			.token(token)
			.operator(operator)
			.operand(operand)
			.build();
	}
}
