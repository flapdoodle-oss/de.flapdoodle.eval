package de.flapdoodle.eval.evaluatables;

import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
public abstract class TypedEvaluatablesMap implements TypedEvaluatableByName {
	protected abstract Map<String, TypedEvaluatableByNumberOfArguments> map();

	@Override
	@Value.Auxiliary
	public Optional<? extends TypedEvaluatableByArguments> find(String name, int numberOfArguments) {
		return Optional.ofNullable(map().get(name))
			.flatMap(it -> it.filterByNumberOfArguments(numberOfArguments));
	}

	public static ImmutableTypedEvaluatablesMap.Builder builder() {
		return ImmutableTypedEvaluatablesMap.builder();
	}
}
