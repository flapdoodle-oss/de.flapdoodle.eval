package de.flapdoodle.eval.evaluatables;

import de.flapdoodle.eval.EvaluatableException;
import de.flapdoodle.types.Either;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class TypedEvaluatables implements TypedEvaluatableByArguments, TypedEvaluatableByNumberOfArguments {
	protected abstract List<TypedEvaluatable<?>> list();

	@Override
	@Value.Auxiliary
	public Optional<? extends TypedEvaluatableByArguments> filterByNumberOfArguments(int numberOfArguments) {
		List<TypedEvaluatable<?>> filtered = list().stream()
			.filter(entry -> entry.signature().minNumberOfArguments() <= numberOfArguments && entry.signature().maxNumberOfArguments() >= numberOfArguments)
			.collect(Collectors.toList());
		return !filtered.isEmpty()
			? Optional.of(builder().list(filtered).build())
			: Optional.empty();
	}

	@Override
	@Value.Auxiliary
	public Either<TypedEvaluatable<?>, List<EvaluatableException>> find(List<? extends de.flapdoodle.eval.values.Value<?>> values) {
		List<EvaluatableException> errors = new ArrayList<>();
		for (TypedEvaluatable<?> evaluatable : list()) {
			Optional<EvaluatableException> error = evaluatable.signature().validateArguments(values);
			if (error.isPresent()) errors.add(error.get());
			else return Either.left(evaluatable);
		}

		return Either.right(errors);
	}


	public static ImmutableTypedEvaluatables.Builder builder() {
		return ImmutableTypedEvaluatables.builder();
	}

	public static abstract class Wrapper implements TypedEvaluatableByNumberOfArguments {

		private final TypedEvaluatables delegate;

		public Wrapper(TypedEvaluatables delegate) {
			this.delegate = delegate;
		}

		@Override
		public final Optional<? extends TypedEvaluatableByArguments> filterByNumberOfArguments(int numberOfArguments) {
			return delegate.filterByNumberOfArguments(numberOfArguments);
		}
	}
}
