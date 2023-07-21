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

import java.util.Arrays;
import java.util.List;

@org.immutables.value.Value.Immutable
public abstract class ValueArray {
	protected abstract List<Value<?>> values();

	@org.immutables.value.Value.Auxiliary
	public Value<?> get(int index) {
		return values().get(index);
	}
	
	public static ValueArray of(Iterable<? extends Value<?>> values) {
		return ImmutableValueArray.builder()
			.values(values)
			.build();
	}

	public static ValueArray of(Value<?> ... values) {
		return builder()
			.values(Arrays.asList(values))
			.build();
	}

	public static ImmutableValueArray.Builder builder() {
		return ImmutableValueArray.builder();
	}
}
