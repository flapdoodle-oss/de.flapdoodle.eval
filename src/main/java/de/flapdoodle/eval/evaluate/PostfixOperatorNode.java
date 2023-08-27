package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.PostfixOperator;
import de.flapdoodle.eval.parser.Token;


@org.immutables.value.Value.Immutable
public abstract class PostfixOperatorNode extends Node {
	@org.immutables.value.Value.Parameter
	protected abstract PostfixOperator operator();
	@org.immutables.value.Value.Parameter
	protected abstract Node operand();
	
	@Override
	public Value<?> evaluate(ValueResolver variableResolver, EvaluationContext context) throws EvaluationException {
		return operator().evaluate(variableResolver, context, token(), operand().evaluate(variableResolver, context));
	}

	public static PostfixOperatorNode of(Token token, PostfixOperator operator, Node operand) {
		return ImmutablePostfixOperatorNode.builder()
			.token(token)
			.operator(operator)
			.operand(operand)
			.build();
	}
}