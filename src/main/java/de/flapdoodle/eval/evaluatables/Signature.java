package de.flapdoodle.eval.evaluatables;

import de.flapdoodle.eval.EvaluatableException;
import de.flapdoodle.eval.values.Value;
import org.immutables.builder.Builder;

import java.util.List;
import java.util.Optional;

@org.immutables.value.Value.Immutable
public abstract class Signature<T extends Value<?>> {
	public abstract List<Parameter<?>> parameters();

	// vararg is at least ONE value, zero values are not allowed
	@org.immutables.value.Value.Default
	public boolean isVarArg() {
		return false;
	}

	@Builder.Parameter
	public abstract Class<T> returnType();

	@org.immutables.value.Value.Derived
	public int minNumberOfArguments() {
		return parameters().size();
	}

	@org.immutables.value.Value.Derived
	public int maxNumberOfArguments() {
		return isVarArg()
			? Integer.MAX_VALUE
			: parameters().size();
	}

	@org.immutables.value.Value.Auxiliary
	public Parameter<?> get(int index) {
		if (isVarArg() && index >= parameters().size()) {
			index = parameters().size() - 1;
		}
		return parameters().get(index);
	}

	@org.immutables.value.Value.Auxiliary
	public Optional<EvaluatableException> validateArguments(List<? extends Value<?>> arguments) {
		if (minNumberOfArguments() > arguments.size()) return Optional.of(EvaluatableException.of("not enough(<%s) arguments: %s", minNumberOfArguments(), arguments.size()));
		if (arguments.size() > maxNumberOfArguments()) return Optional.of(EvaluatableException.of("to many(>%s) arguments: ", maxNumberOfArguments(), arguments.size()));

		for (int i = 0; i < minNumberOfArguments(); i++) {
			Value<?> value = arguments.get(i);
			Parameter<?> parameter = get(i);
			Class<?> type = parameter.type();

			if (!type.isInstance(value)) return Optional.of(EvaluatableException.of("wrong type: %s != %s", type, value));
			Optional<EvaluatableException> error = parameter.validationError(value);
			if (error.isPresent()) return error;
		}
		if (isVarArg()) {
			Parameter<?> parameter = get(minNumberOfArguments() - 1);
			Class<?> type = parameter.type();
			for (int i = minNumberOfArguments() + 1; i < arguments.size(); i++) {
				Value<?> value = arguments.get(i);
				if (!type.isInstance(value)) return Optional.of(EvaluatableException.of("wrong type: %s != %s", type, value));
				Optional<EvaluatableException> error = parameter.validationError(value);
				if (error.isPresent()) return error;
			}
		}
		return Optional.empty();
	}

	public static <T extends Value<?>> Signature<T> of(Class<T> returnType, List<? extends Parameter<?>> parameters) {
		return ImmutableSignature.builder(returnType)
			.addAllParameters(parameters)
			.build();
	}

	public static <T extends Value<?>> Signature<T> of(Class<T> returnType, Parameter<?>... parameters) {
		return ImmutableSignature.builder(returnType)
			.addParameters(parameters)
			.build();
	}

	public static <T extends Value<?>> Signature<T> ofVarArg(Class<T> returnType, Parameter<?>... parameters) {
		return ImmutableSignature.builder(returnType)
			.addParameters(parameters)
			.isVarArg(true)
			.build();
	}
}
