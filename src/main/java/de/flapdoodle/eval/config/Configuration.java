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

import de.flapdoodle.eval.data.VariableResolver;
import de.flapdoodle.eval.functions.Function;
import de.flapdoodle.eval.operators.Operator;
import de.flapdoodle.types.Pair;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@Value.Immutable
public abstract class Configuration {

	public static final Map<String, de.flapdoodle.eval.data.Value<?>> StandardConstants = Collections.unmodifiableMap(standardConstants());

	@Value.Default
	public MathContext getMathContext() {
		return new MathContext(68, RoundingMode.HALF_EVEN);
	}

	@Value.Default
	public OperatorResolver getOperatorResolver() {
		return OperatorResolver.defaults();
	}

	@Value.Default
	public FunctionResolver getFunctionResolver() {
		return FunctionResolver.defaults();
	}

	@Value.Default
	public VariableResolver getConstantResolver() {
		return VariableResolver.builder()
				.withValues(standardConstants())
				.build();
	}

	private static Map<String, de.flapdoodle.eval.data.Value<?>> standardConstants() {
		Map<String, de.flapdoodle.eval.data.Value<?>> constants = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		constants.put("TRUE", de.flapdoodle.eval.data.Value.of(true));
		constants.put("FALSE", de.flapdoodle.eval.data.Value.of(false));
		constants.put(
			"PI",
			de.flapdoodle.eval.data.Value.of(
				new BigDecimal(
					"3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679")));
		constants.put(
			"E",
			de.flapdoodle.eval.data.Value.of(
				new BigDecimal(
					"2.71828182845904523536028747135266249775724709369995957496696762772407663")));
		constants.put("NULL", de.flapdoodle.eval.data.Value.ofNull());
		return constants;
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

	// TODO its override, not addition
	@SafeVarargs @Value.Auxiliary
	public final ImmutableConfiguration withAdditionalOperators(Pair<String, Operator>... operators) {
		return ImmutableConfiguration.copyOf(this)
			.withOperatorResolver(MapBasedOperatorResolver.of(operators)
				.andThen(getOperatorResolver()));
	}

	// TODO its override, not addition
	@SafeVarargs @Value.Auxiliary
	public final ImmutableConfiguration withAdditionalFunctions(Pair<String, Function>... functions) {
		return ImmutableConfiguration.copyOf(this)
			.withFunctionResolver(MapBasedFunctionResolver.of(functions)
				.andThen(getFunctionResolver()));
	}

	public ImmutableConfiguration withAdditionalFunctions(String name, Function function) {
		return withAdditionalFunctions(Pair.of(name, function));
	}

	public static ImmutableConfiguration defaultConfiguration() {
		return builder().build();
	}

	public static ImmutableConfiguration.Builder builder() {
		return ImmutableConfiguration.builder();
	}
}
