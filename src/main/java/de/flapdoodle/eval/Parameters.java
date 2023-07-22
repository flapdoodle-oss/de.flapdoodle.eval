package de.flapdoodle.eval;

import de.flapdoodle.eval.parser.Token;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class Parameters {
	public abstract List<Parameter<?>> list();

	@Value.Check
	protected void check() {
		for (int i = 0; i < list().size() - 1; i++) {
			Parameter<?> it = list().get(i);
			if (it.isVarArg()) {
				throw new IllegalArgumentException(
					"Only last parameter may be defined as variable argument");
			}
			if (it.isOptional()) {
				throw new IllegalArgumentException(
					"Only last parameter may be defined as optional argument");
			}
		}
	}

	@Value.Derived
	public int min() {
		if (list().isEmpty()) return 0;
		Parameter<?> last = list().get(list().size() - 1);
		return list().size() - (last.isOptional() ? 1 : 0);
	}

	@Value.Derived
	public int max() {
		if (list().isEmpty()) return 0;
		Parameter<?> last = list().get(list().size() - 1);
		return last.isVarArg()
			? Integer.MAX_VALUE
			: list().size();
	}

	@Value.Derived
	public boolean hasVarArgs() {
		return !list().isEmpty() && list().get(list().size() - 1).isVarArg();
	}

	@Value.Derived
	public boolean hasOptional() {
		return !list().isEmpty() && list().get(list().size() - 1).isOptional();
	}

	@Value.Auxiliary
	public boolean isLazy(int parameterIndex) {
		return get(parameterIndex).isLazy();
	}

	@Value.Auxiliary
	public Parameter<?> get(int index) {
		if (hasVarArgs() && index >= list().size()) {
			index = list().size() - 1;
		}
		return list().get(index);
	}

	public void validate(Token token, List<de.flapdoodle.eval.data.Value<?>> parameterValues) throws EvaluationException {
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

	public static ImmutableParameters of(Parameter<?> first, Parameter<?>... all) {
		return ImmutableParameters.builder()
			.addList(first)
			.addList(all)
			.build();
	}

	public static ImmutableParameters of(Iterable<? extends Parameter<?>> all) {
		return ImmutableParameters.builder()
			.addAllList(all)
			.build();
	}
}
