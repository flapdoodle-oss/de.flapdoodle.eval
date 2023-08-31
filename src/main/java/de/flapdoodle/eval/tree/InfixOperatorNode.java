package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.InfixOperator;
import de.flapdoodle.eval.parser.Token;

@org.immutables.value.Value.Immutable
public abstract class InfixOperatorNode extends Node {
	@org.immutables.value.Value.Parameter
	protected abstract InfixOperator operator();
	@org.immutables.value.Value.Parameter
	protected abstract Node leftOperand();
	@org.immutables.value.Value.Parameter
	protected abstract Node rightOperand();

	@Override
	public Value<?> evaluate(ValueResolver variableResolver, EvaluationContext context) throws EvaluationException {
		return operator().evaluate(variableResolver,context, token(),
			leftOperand().evaluate(variableResolver, context),
			rightOperand().evaluate(variableResolver, context));
	}
	
	public static InfixOperatorNode of(Token token, InfixOperator operator, Node left, Node right) {
		return ImmutableInfixOperatorNode.builder()
			.token(token)
			.operator(operator)
			.leftOperand(left)
			.rightOperand(right)
			.build();
	}
}
