package de.flapdoodle.eval;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class Parameters {
	public abstract List<Parameter<?>> list();

	@Value.Default
	public boolean isVarArg() {
		return false;
//		return !list().isEmpty() && list().get(list().size() - 1).isVarArg();
	}

	@Value.Default
	public boolean hasOptional() {
		return false;
//		return !list().isEmpty() && list().get(list().size() - 1).isOptional();
	}

	@Value.Check
	protected void check() {
		if (isVarArg() && hasOptional()) {
			throw new IllegalArgumentException("varArg and optional is not of any use");
		}
	}

	@Value.Derived
	public int min() {
		if (list().isEmpty()) return 0;
		return list().size() - (hasOptional() ? 1 : 0);
	}

	@Value.Derived
	public int max() {
		if (list().isEmpty()) return 0;
		return isVarArg()
			? Integer.MAX_VALUE
			: list().size();
	}

	@Value.Auxiliary
	public boolean isLazy(int parameterIndex) {
		return get(parameterIndex).isLazy();
	}

	@Value.Auxiliary
	public Parameter<?> get(int index) {
		if (isVarArg() && index >= list().size()) {
			index = list().size() - 1;
		}
		return list().get(index);
	}

	public void validate(CommonToken token, List<de.flapdoodle.eval.data.Value<?>> parameterValues) throws EvaluationException {
		if (parameterValues.size() < min()) {
			throw new EvaluationException(token, "not enough parameters: " + parameterValues.size() + " < " + min());
		}
		if (parameterValues.size() > max()) {
			throw new EvaluationException(token, "to many parameters: " + parameterValues.size() + " > " + max());
		}
		for (int i = 0; i < parameterValues.size(); i++) {
			Parameter<?> definition = get(i);
			definition.validate(token, parameterValues.get(i));
		}
	}

	public static ImmutableParameters.Builder builder() {
		return ImmutableParameters.builder();
	}

	public static ImmutableParameters of(Parameter<?>... all) {
		return ImmutableParameters.builder()
			.addList(all)
			.build();
	}

//	public static ImmutableParameters of(Parameter<?> first, Parameter<?>... all) {
//		return ImmutableParameters.builder()
//			.addList(first)
//			.addList(all)
//			.build();
//	}

	public static ImmutableParameters of(Iterable<? extends Parameter<?>> all) {
		return ImmutableParameters.builder()
			.addAllList(all)
			.build();
	}

	public static ImmutableParameters optionalWith(Parameter<?>... all) {
		return ImmutableParameters.builder()
			.addList(all)
			.hasOptional(true)
			.build();
	}

	public static ImmutableParameters varArgWith(Parameter<?> ... all) {
		return ImmutableParameters.builder()
			.addList(all)
			.isVarArg(true)
			.build();
	}
}
