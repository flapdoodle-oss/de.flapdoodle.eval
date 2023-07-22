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

import de.flapdoodle.eval.functions.Function;
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
	public FunctionResolver getFunctionResolver() {
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
	public boolean isImplicitMultiplicationAllowed() {
		return true;
	}

	@Value.Default
	public boolean isArraysAllowed() {
		return true;
	}

	@Value.Default
	public boolean isStructuresAllowed() {
		return true;
	}

	@Value.Default
	public boolean isAllowOverwriteConstants() {
		return true;
	}

	@SafeVarargs @Value.Auxiliary
	public final ImmutableConfiguration withOperators(Pair<String, Operator>... operators) {
		return ImmutableConfiguration.copyOf(this)
			.withOperatorResolver(MapBasedOperatorResolver.of(operators)
				.andThen(getOperatorResolver()));
	}

	@SafeVarargs @Value.Auxiliary
	public final ImmutableConfiguration witFunctions(Pair<String, Function>... functions) {
		return ImmutableConfiguration.copyOf(this)
			.withFunctionResolver(MapBasedFunctionResolver.of(functions)
				.andThen(getFunctionResolver()));
	}

	public ImmutableConfiguration witFunction(String name, Function function) {
		return witFunctions(Pair.of(name, function));
	}

	public static ImmutableConfiguration defaultConfiguration() {
		return builder().build();
	}

	public static ImmutableConfiguration.Builder builder() {
		return ImmutableConfiguration.builder();
	}
}
