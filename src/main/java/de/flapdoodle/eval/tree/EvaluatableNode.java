package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.VariableResolver;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluableByArguments;
import de.flapdoodle.eval.exceptions.EvaluableException;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.types.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@org.immutables.value.Value.Immutable
public abstract class EvaluatableNode extends Node {
	@org.immutables.value.Value.Parameter
	protected abstract TypedEvaluableByArguments evaluatable();
	@org.immutables.value.Value.Parameter
	protected abstract List<Node> parameters();
	@org.immutables.value.Value.Parameter
	protected abstract Function<EvaluationException, Object> exceptionAsParameter();
	@org.immutables.value.Value.Parameter
	protected abstract Function<Object, Optional<EvaluationException>> matchException();

	@Override
	public Object evaluate(VariableResolver variableResolver, EvaluationContext context) throws EvaluationException {
		List<Object> parameterResults = new ArrayList<>();
		for (int i = 0; i < parameters().size(); i++) {
			Node parameter = parameters().get(i);
			try {
				parameterResults.add(parameter.evaluate(variableResolver, context));
			} catch (EvaluationException ex) {
				parameterResults.add(exceptionAsParameter().apply(ex));
			}
		}
		Either<TypedEvaluable<?>, List<EvaluableException>> evaluatable = evaluatable().find(parameterResults);
		if (evaluatable.isLeft()) {
			try {
				Object evaluated = evaluatable.left().evaluate(variableResolver, context, token(), parameterResults);
				Optional<EvaluationException> matchedException = matchException().apply(evaluated);
				if (matchedException.isPresent()) {
					throw matchedException.get();
				}
				return evaluated;
			} catch (EvaluationException.AsRuntimeException wrapping) {
				throw wrapping.wrapped();
			}
		} else {
			throw new EvaluationException(token(), evaluatable.right().get(0));
		}
	}
	
	public static EvaluatableNode of(
		Token token,
		TypedEvaluableByArguments function,
		List<Node> parameters,
		Function<EvaluationException, Object> exceptionAsParameter,
		Function<Object, Optional<EvaluationException>> matchException) {
		return ImmutableEvaluatableNode.builder()
			.token(token)
			.evaluatable(function)
			.parameters(parameters)
			.exceptionAsParameter(exceptionAsParameter)
			.matchException(matchException)
			.build();
	}
}
