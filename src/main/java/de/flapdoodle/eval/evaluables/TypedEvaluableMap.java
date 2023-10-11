package de.flapdoodle.eval.evaluables;

import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
public abstract class TypedEvaluableMap implements TypedEvaluableByName {
	protected abstract Map<String, TypedEvaluableByNumberOfArguments> map();

	@Override
	@Value.Auxiliary
	public Optional<? extends TypedEvaluableByArguments> find(String name, int numberOfArguments) {
		return Optional.ofNullable(map().get(name))
			.flatMap(it -> it.filterByNumberOfArguments(numberOfArguments));
	}

	public static ImmutableTypedEvaluableMap.Builder builder() {
		return ImmutableTypedEvaluableMap.builder();
	}
}
