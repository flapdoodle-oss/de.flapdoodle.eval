/**
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
package de.flapdoodle.eval.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

public class VariableResolverBuilder {
	private final Map<String, Value<?>> variables =
		new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	private VariableResolverBuilder() {

	}

	public static VariableResolverBuilder newInstance() {
		return new VariableResolverBuilder();
	}

	public VariableResolverBuilder with(String variable, Value<?> value) {
		Value<?> old = variables.put(variable, value);
		if (old!=null) {
			throw new IllegalArgumentException("was already set to "+old);
		}
		return this;
	}

	public VariableResolverBuilder withNull(String variable) {
		return with(variable, Value.ofNull());
	}

	@Deprecated
	public VariableResolverBuilder with(String variable, ValueMap value) {
		return with(variable, Value.of(value));
	}

	@Deprecated
	public VariableResolverBuilder with(String variable, ValueArray value) {
		return with(variable, Value.of(value));
	}

	@Deprecated
	public VariableResolverBuilder with(String variable, BigDecimal value) {
		return with(variable, Value.of(value));
	}

	@Deprecated
	public VariableResolverBuilder with(String variable, boolean value) {
		return with(variable, Value.of(value));
	}

	@Deprecated
	public VariableResolverBuilder with(String variable, double value) {
		return with(variable, Value.of(value));
	}

	@Deprecated
	public VariableResolverBuilder with(String variable, String value) {
		return with(variable, Value.of(value));
	}

	@Deprecated
	public <T> VariableResolverBuilder with(String variable, Function<T, Value<?>> mapper, Collection<T> collection) {
		return with(variable, Value.of(mapper, collection));
	}

	@Deprecated
	public <T> VariableResolverBuilder with(String variable, Function<T, Value<?>> mapper, Map<String, T> collection) {
		return with(variable, Value.of(mapper, collection));
	}

	public VariableResolverBuilder and(String variable, Value<?> value) {
		return with(variable, value);
	}

	public VariableResolverBuilder withValues(Map<String, ? extends Value<?>> values) {
		for (Map.Entry<String, ? extends Value<?>> entry : values.entrySet()) {
			with(entry.getKey(), entry.getValue());
		}
		return this;
	}


	public VariableResolver build() {
		TreeMap<String, Value<?>> clone = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		clone.putAll(variables);
		return clone::get;
	}
}
