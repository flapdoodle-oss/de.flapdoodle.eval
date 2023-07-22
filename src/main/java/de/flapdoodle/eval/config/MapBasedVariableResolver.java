package de.flapdoodle.eval.config;

import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.data.ValueArray;
import de.flapdoodle.eval.data.ValueMap;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Immutable
public abstract class MapBasedVariableResolver implements VariableResolver {
	protected abstract Map<String, Value<?>> variables();

	@Lazy
	protected Map<String, String> lowerCaseToKey() {
		return variables().keySet().stream().collect(Collectors.toMap(String::toLowerCase, Function.identity()));
	}

	@Auxiliary
	@Override
	public Value<?> getData(String variable) {
		return variables().get(lowerCaseToKey().get(variable.toLowerCase()));
	}

	@Auxiliary
	public ImmutableMapBasedVariableResolver with(String variable, Value<?> value) {
		return builder().from(this)
			.putVariables(variable, value)
			.build();
	}

	@Auxiliary
	public ImmutableMapBasedVariableResolver withNull(String variable) {
		return with(variable, Value.ofNull());
	}

	@Auxiliary
	@Deprecated
	public ImmutableMapBasedVariableResolver with(String variable, ValueMap value) {
		return with(variable, Value.of(value));
	}

	@Auxiliary
	@Deprecated
	public ImmutableMapBasedVariableResolver with(String variable, ValueArray value) {
		return with(variable, Value.of(value));
	}

	@Auxiliary
	@Deprecated
	public ImmutableMapBasedVariableResolver with(String variable, BigDecimal value) {
		return with(variable, Value.of(value));
	}

	@Auxiliary
	@Deprecated
	public ImmutableMapBasedVariableResolver with(String variable, boolean value) {
		return with(variable, Value.of(value));
	}

	@Auxiliary
	@Deprecated
	public ImmutableMapBasedVariableResolver with(String variable, double value) {
		return with(variable, Value.of(value));
	}

	@Auxiliary
	@Deprecated
	public ImmutableMapBasedVariableResolver with(String variable, String value) {
		return with(variable, Value.of(value));
	}

	@Auxiliary
	@Deprecated
	public <T> ImmutableMapBasedVariableResolver with(String variable, Function<T, Value<?>> mapper, Collection<T> collection) {
		return with(variable, Value.of(mapper, collection));
	}

	@Auxiliary
	@Deprecated
	public <T> ImmutableMapBasedVariableResolver with(String variable, Function<T, Value<?>> mapper, Map<String, T> collection) {
		return with(variable, Value.of(mapper, collection));
	}

	@Auxiliary
	public ImmutableMapBasedVariableResolver and(String variable, Value<?> value) {
		return with(variable, value);
	}

	@Auxiliary
	public ImmutableMapBasedVariableResolver withValues(Map<String, ? extends Value<?>> values) {
		ImmutableMapBasedVariableResolver.Builder builder = builder().from(this);
		for (Map.Entry<String, ? extends Value<?>> entry : values.entrySet()) {
			builder.putVariables(entry.getKey(), entry.getValue());
		}
		return builder.build();
	}

	public static ImmutableMapBasedVariableResolver.Builder builder() {
		return ImmutableMapBasedVariableResolver.builder();
	}

	public static ImmutableMapBasedVariableResolver empty() {
		return builder().build();
	}
}
