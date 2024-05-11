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
import de.flapdoodle.reflection.TypeInfo;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;

import java.util.Map;

@Immutable
public abstract class MapBasedVariableTypeResolver implements VariableTypeResolver {
	protected abstract Map<String, TypeInfo<?>> variables();

	@Auxiliary
	@Override
	public TypeInfo<?> get(String variable) {
		return variables().get(variable);
	}

	@Auxiliary
	public ImmutableMapBasedVariableTypeResolver with(String variable, TypeInfo<?> value) {
		return builder().from(this)
			.putVariables(variable, value)
			.build();
	}

	@Auxiliary
	public ImmutableMapBasedVariableTypeResolver and(String variable, TypeInfo<?> value) {
		return with(variable, value);
	}

	@Auxiliary
	public ImmutableMapBasedVariableTypeResolver withValues(Map<String, ? extends TypeInfo<?>> values) {
		ImmutableMapBasedVariableTypeResolver.Builder builder = builder().from(this);
		for (Map.Entry<String, ? extends TypeInfo<?>> entry : values.entrySet()) {
			builder.putVariables(entry.getKey(), entry.getValue());
		}
		return builder.build();
	}

	public static ImmutableMapBasedVariableTypeResolver.Builder builder() {
		return ImmutableMapBasedVariableTypeResolver.builder();
	}

	public static ImmutableMapBasedVariableTypeResolver empty() {
		return builder().build();
	}
}
