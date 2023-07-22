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
package de.flapdoodle.eval.config;

import de.flapdoodle.eval.data.Value;

public interface VariableResolver {
	Value<?> getData(String variable);

	default VariableResolver andThen(VariableResolver fallback) {
		VariableResolver that = this;
		return variable -> {
			Value<?> ret = that.getData(variable);
			return ret!=null ? ret : fallback.getData(variable);
		};
	}

	default boolean has(String name) {
		return getData(name) != null;
	}

	static ImmutableMapBasedVariableResolver empty() {
		return MapBasedVariableResolver.empty();
	}
}
