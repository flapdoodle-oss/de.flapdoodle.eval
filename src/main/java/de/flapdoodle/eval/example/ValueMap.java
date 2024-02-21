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
package de.flapdoodle.eval.example;

import java.util.Map;

@org.immutables.value.Value.Immutable
public abstract class ValueMap {
	protected abstract Map<String, Value<?>> values();

	@org.immutables.value.Value.Auxiliary
	public boolean containsKey(String name) {
		return values().containsKey(name);
	}

	@org.immutables.value.Value.Auxiliary
	public Value<?> get(String name) {
		return values().get(name);
	}

	public static ImmutableValueMap.Builder builder() {
		return ImmutableValueMap.builder();
	}

	public static ImmutableValueMap of(Map<String, Value<?>> map) {
		return builder()
			.putAllValues(map)
			.build();
	}
}
