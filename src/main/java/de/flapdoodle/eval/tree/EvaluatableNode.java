package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.EvaluatableException;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatableByArguments;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.types.Either;

import java.util.ArrayList;
import java.util.List;

@org.immutables.value.Value.Immutable
public abstract class EvaluatableNode extends Node {
	@org.immutables.value.Value.Parameter
	protected abstract TypedEvaluatableByArguments evaluatable();
	@org.immutables.value.Value.Parameter
	protected abstract List<Node> parameters();

	@Override
	public Value<?> evaluate(ValueResolver variableResolver, EvaluationContext context) throws EvaluationException {
		List<Value<?>> parameterResults = new ArrayList<>();
		for (int i = 0; i < parameters().size(); i++) {
			Node parameter = parameters().get(i);
			try {
				parameterResults.add(parameter.evaluate(variableResolver, context));
			} catch (EvaluationException ex) {
				parameterResults.add(Value.failedWith(ex));
			}
		}
		Either<TypedEvaluatable<?>, List<EvaluatableException>> evaluatable = evaluatable().find(parameterResults);
		if (evaluatable.isLeft()) {
			try {
				Value<?> evaluated = evaluatable.left().evaluate(variableResolver, context, token(), parameterResults);
				if (evaluated instanceof Value.FailedWithException) {
					throw ((Value.FailedWithException<?>) evaluated).exception();
				}
				return evaluated;
			} catch (EvaluationException.AsRuntimeException wrapping) {
				throw wrapping.wrapped();
			}
		} else {
			throw new EvaluationException(token(), evaluatable.right().get(0));
		}
	}
	
	public static EvaluatableNode of(Token token, TypedEvaluatableByArguments function, List<Node> parameters) {
		return ImmutableEvaluatableNode.builder()
			.token(token)
			.evaluatable(function)
			.parameters(parameters)
			.build();
	}
}
