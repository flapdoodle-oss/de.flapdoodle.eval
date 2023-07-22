/**
 * Copyright (C) 2023
 * Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.eval.config;

import de.flapdoodle.types.Pair;
import org.immutables.value.Value;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class MapBasedEvaluateableResolver implements EvaluateableResolver {
	protected abstract Map<String, de.flapdoodle.eval.Evaluateable> map();

	@Value.Lazy
	protected Map<String, String> lowerCaseToKey() {
		return map().keySet().stream().collect(Collectors.toMap(String::toLowerCase, Function.identity()));
	}

	@Value.Auxiliary
	@Override
	public de.flapdoodle.eval.Evaluateable get(String functionName) {
		return map().get(lowerCaseToKey().get(functionName.toLowerCase()));
	}

	public static ImmutableMapBasedEvaluateableResolver of(Pair<String, ? extends de.flapdoodle.eval.Evaluateable>... entries) {
		ImmutableMapBasedEvaluateableResolver.Builder builder = ImmutableMapBasedEvaluateableResolver.builder();
		for (Pair<String, ? extends de.flapdoodle.eval.Evaluateable> entry : entries) {
			builder.putMap(entry.first(), entry.second());
		}
		return builder.build();
	}

	public static ImmutableMapBasedEvaluateableResolver.Builder builder() {
		return ImmutableMapBasedEvaluateableResolver.builder();
	}
}
