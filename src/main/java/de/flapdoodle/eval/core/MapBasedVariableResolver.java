package de.flapdoodle.eval.core;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;

import java.util.Map;

@Immutable
public abstract class MapBasedVariableResolver implements VariableResolver {
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
	public ImmutableMapBasedVariableResolver with(String variable, Object value) {
		return builder().from(this)
			.putVariables(variable, value)
			.build();
	}

	@Auxiliary
	public ImmutableMapBasedVariableResolver and(String variable, Object value) {
		return with(variable, value);
	}

	@Auxiliary
	public ImmutableMapBasedVariableResolver withValues(Map<String, ?> values) {
		ImmutableMapBasedVariableResolver.Builder builder = builder().from(this);
		for (Map.Entry<String, ?> entry : values.entrySet()) {
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
