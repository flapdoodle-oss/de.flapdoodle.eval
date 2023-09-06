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
public abstract class MapBasedValueResolver implements ValueResolver {
	protected abstract Map<String, Value<?>> variables();

//	@Lazy
//	protected Map<String, String> lowerCaseToKey() {
//		return variables().keySet().stream().collect(Collectors.toMap(String::toLowerCase, Function.identity()));
//	}

	@Auxiliary
	@Override
	public Value<?> get(String variable) {
		return variables().get(variable);
	}

	@Auxiliary
	public ImmutableMapBasedValueResolver with(String variable, Value<?> value) {
		return builder().from(this)
			.putVariables(variable, value)
			.build();
	}

	@Auxiliary
	public ImmutableMapBasedValueResolver withNull(String variable) {
		return with(variable, Value.ofNull());
	}

	@Auxiliary
	@Deprecated
	public ImmutableMapBasedValueResolver with(String variable, ValueMap value) {
		return with(variable, Value.of(value));
	}

	@Auxiliary
	@Deprecated
	public ImmutableMapBasedValueResolver with(String variable, ValueArray value) {
		return with(variable, Value.of(value));
	}

	@Auxiliary
	@Deprecated
	public ImmutableMapBasedValueResolver with(String variable, BigDecimal value) {
		return with(variable, Value.of(value));
	}

	@Auxiliary
	@Deprecated
	public ImmutableMapBasedValueResolver with(String variable, boolean value) {
		return with(variable, Value.of(value));
	}

	@Auxiliary
	@Deprecated
	public ImmutableMapBasedValueResolver with(String variable, double value) {
		return with(variable, Value.of(value));
	}

	@Auxiliary
	@Deprecated
	public ImmutableMapBasedValueResolver with(String variable, String value) {
		return with(variable, Value.of(value));
	}

	@Auxiliary
	@Deprecated
	public <T> ImmutableMapBasedValueResolver with(String variable, Function<T, Value<?>> mapper, Collection<T> collection) {
		return with(variable, Value.of(mapper, collection));
	}

	@Auxiliary
	@Deprecated
	public <T> ImmutableMapBasedValueResolver with(String variable, Function<T, Value<?>> mapper, Map<String, T> collection) {
		return with(variable, Value.of(mapper, collection));
	}

	@Auxiliary
	public ImmutableMapBasedValueResolver and(String variable, Value<?> value) {
		return with(variable, value);
	}

	@Auxiliary
	public ImmutableMapBasedValueResolver withValues(Map<String, ? extends Value<?>> values) {
		ImmutableMapBasedValueResolver.Builder builder = builder().from(this);
		for (Map.Entry<String, ? extends Value<?>> entry : values.entrySet()) {
			builder.putVariables(entry.getKey(), entry.getValue());
		}
		return builder.build();
	}

	public static ImmutableMapBasedValueResolver.Builder builder() {
		return ImmutableMapBasedValueResolver.builder();
	}

	public static ImmutableMapBasedValueResolver empty() {
		return builder().build();
	}
}
