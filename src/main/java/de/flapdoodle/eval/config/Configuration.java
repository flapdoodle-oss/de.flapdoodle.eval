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

import de.flapdoodle.eval.Evaluateable;
import de.flapdoodle.eval.operators.Operator;
import de.flapdoodle.types.Pair;
import org.immutables.value.Value;

import java.math.MathContext;
import java.time.ZoneId;

@Value.Immutable
public abstract class Configuration {

	@Value.Default
	public MathContext getMathContext() {
		return Defaults.mathContext();
	}

	@Value.Default
	public OperatorResolver getOperatorResolver() {
		return Defaults.operators();
	}

	@Value.Default
	public Operators operators() { return Defaults.operators().operators(); }

	@Value.Default
	public EvaluateableResolver functions() {
		return Defaults.functions();
	}

	@Value.Default
	public ValueResolver getConstantResolver() {
		return Defaults.constants();
	}

	@Value.Default
	public ZoneId getDefaultZoneId() {
		return ZoneId.systemDefault();
	}

	@Value.Default
	public boolean isAllowOverwriteConstants() {
		return true;
	}

	@SafeVarargs
	@Value.Auxiliary
	public final ImmutableConfiguration withOperators(Pair<String, Operator>... operators) {
		ImmutableMapBasedOperatorResolver newOperatorResolver = MapBasedOperatorResolver.of(operators);
		return ImmutableConfiguration.copyOf(this)
			.withOperatorResolver(newOperatorResolver
				.andThen(getOperatorResolver()))
			.withOperators(newOperatorResolver.operators()
				.andThen(operators()));
	}

	@SafeVarargs
	public final ImmutableConfiguration withFunctions(Pair<String, ? extends Evaluateable>... functions) {
		return ImmutableConfiguration.copyOf(this)
			.withFunctions(MapBasedEvaluateableResolver.of(functions)
				.andThen(functions()));
	}

	public ImmutableConfiguration withFunction(String name, Evaluateable function) {
		return withFunctions(Pair.of(name, function));
	}

	public static ImmutableConfiguration defaultConfiguration() {
		return builder().build();
	}

	public static ImmutableConfiguration.Builder builder() {
		return ImmutableConfiguration.builder();
	}
}
