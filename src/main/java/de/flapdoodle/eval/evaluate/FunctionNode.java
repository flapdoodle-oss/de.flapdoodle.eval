package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.Evaluateable;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.util.ArrayList;
import java.util.List;

@org.immutables.value.Value.Immutable
public abstract class FunctionNode extends Node {
	@org.immutables.value.Value.Parameter
	protected abstract Evaluateable function();
	@org.immutables.value.Value.Parameter
	protected abstract List<Node> parameters();

	@Override
	public Value<?> evaluate(ValueResolver variableResolver, EvaluationContext context) throws EvaluationException {
		List<Value<?>> parameterResults = new ArrayList<>();
		for (int i = 0; i < parameters().size(); i++) {
			Node parameter = parameters().get(i);
			if (function().parameterIsLazy(i)) {
				try {
					parameterResults.add(parameter.evaluate(variableResolver, context));
				} catch (EvaluationException ex) {
					parameterResults.add(Value.failedWith(ex));
				}
			} else {
				parameterResults.add(parameter.evaluate(variableResolver, context));
			}
		}

		Value<?> evaluated = function().evaluate(variableResolver, context, token(), parameterResults);
		if (evaluated instanceof Value.FailedWithException) {
			throw ((Value.FailedWithException<?>) evaluated).exception();
		}
		return evaluated;
	}
	
	public static FunctionNode of(Token token, Evaluateable function, List<Node> parameters) {
		return ImmutableFunctionNode.builder()
			.token(token)
			.function(function)
			.parameters(parameters)
			.build();
	}
}
