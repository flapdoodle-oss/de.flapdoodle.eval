/*
 * Copyright (C) 2023
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.eval.core;

import de.flapdoodle.eval.core.evaluables.Evaluated;
import de.flapdoodle.eval.example.Value;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;

import java.util.Map;

@Immutable
public abstract class MapBasedVariableResolver implements VariableResolver {
	protected abstract Map<String, Evaluated<?>> variables();

//	@Lazy
//	protected Map<String, String> lowerCaseToKey() {
//		return variables().keySet().stream().collect(Collectors.toMap(String::toLowerCase, Function.identity()));
//	}

	@Auxiliary
	@Override
	public Evaluated<?> get(String variable) {
		return variables().get(variable);
	}

	@Auxiliary
	public ImmutableMapBasedVariableResolver with(String variable, Evaluated<?> value) {
		return builder().from(this)
			.putVariables(variable, value)
			.build();
	}

	@Auxiliary
	@Deprecated
	// TODO inline
	public ImmutableMapBasedVariableResolver with(String variable, Value<?> value) {
		return with(variable, Evaluated.value(value));
	}

	@Auxiliary
	public ImmutableMapBasedVariableResolver and(String variable, Evaluated<?> value) {
		return with(variable, value);
	}

	@Auxiliary
	@Deprecated
	// TODO inline
	public ImmutableMapBasedVariableResolver and(String variable, Value<?> value) {
		return and(variable, Evaluated.value(value));
	}

	@Auxiliary
	public ImmutableMapBasedVariableResolver withValues(Map<String, ? extends Evaluated<?>> values) {
		ImmutableMapBasedVariableResolver.Builder builder = builder().from(this);
		for (Map.Entry<String, ? extends Evaluated<?>> entry : values.entrySet()) {
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
