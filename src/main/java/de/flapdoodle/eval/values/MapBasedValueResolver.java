package de.flapdoodle.eval.values;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;

import java.util.Map;

@Immutable
public abstract class MapBasedValueResolver implements ValueResolver {
	protected abstract Map<String, Object> variables();

//	@Lazy
//	protected Map<String, String> lowerCaseToKey() {
//		return variables().keySet().stream().collect(Collectors.toMap(String::toLowerCase, Function.identity()));
//	}

	@Auxiliary
	@Override
	public Object get(String variable) {
		return variables().get(variable);
	}

	@Auxiliary
	public ImmutableMapBasedValueResolver with(String variable, Object value) {
		return builder().from(this)
			.putVariables(variable, value)
			.build();
	}

	@Auxiliary
	public ImmutableMapBasedValueResolver and(String variable, Object value) {
		return with(variable, value);
	}

	@Auxiliary
	public ImmutableMapBasedValueResolver withValues(Map<String, ?> values) {
		ImmutableMapBasedValueResolver.Builder builder = builder().from(this);
		for (Map.Entry<String, ?> entry : values.entrySet()) {
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
