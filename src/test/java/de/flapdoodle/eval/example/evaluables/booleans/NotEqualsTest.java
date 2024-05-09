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
package de.flapdoodle.eval.example.evaluables.booleans;

import de.flapdoodle.eval.core.Expression;
import de.flapdoodle.eval.core.MapBasedVariableResolver;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.Evaluated;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.exceptions.ParseException;
import de.flapdoodle.eval.example.BaseEvaluationTest;
import de.flapdoodle.eval.example.Defaults;
import de.flapdoodle.eval.example.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NotEqualsTest extends BaseEvaluationTest {

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"1!=1 : false",
			"1<>1 : false",
			"0!=0 : false",
			"1!=0 : true",
			"0!=1 : true",
			"21.678!=21.678 : false",
			"\"abc\"!=\"abc\" : false",
			"\"abc\"!=\"xyz\" : true",
			"1+2!=4-1 : false",
			"-5.2!=-5.2 :false",
			"localDateTime(2022,10,30)!=localDateTime(2022,10,30) : false",
			"localDateTime(2022,10,30)!=localDateTime(2022,10,28) : true",
			"parseDuration(\"P2D\")!=parseDuration(\"PT24H\") : true"
		})
	void testInfixNotEqualsLiterals(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asBoolean(expectedResult));
	}

	@Test
	void testInfixNotEqualsVariables() throws EvaluationException, ParseException {
		Expression expression = Defaults.expressionFactory().parse("a!=b");

		MapBasedVariableResolver mapBasedValueResolver8 = VariableResolver.empty();
		Value<?> value9 = Value.of(new BigDecimal("1.4"));
		MapBasedVariableResolver mapBasedValueResolver9 = mapBasedValueResolver8.with("a", Evaluated.value(value9));
		Value<?> value8 = Value.of(new BigDecimal("1.4"));
		MapBasedVariableResolver mapBasedVariableResolver4 = mapBasedValueResolver9.with("b", Evaluated.value(value8));
		assertThat(
			expression.evaluate(mapBasedVariableResolver4))
			.isEqualTo(Value.FALSE);

		MapBasedVariableResolver mapBasedValueResolver6 = VariableResolver.empty();
		Value<?> value7 = Value.of("Hello");
		MapBasedVariableResolver mapBasedValueResolver7 = mapBasedValueResolver6.with("a", Evaluated.value(value7));
		Value<?> value6 = Value.of("Hello");
		MapBasedVariableResolver mapBasedVariableResolver3 = mapBasedValueResolver7.with("b", Evaluated.value(value6));
		assertThat(expression.evaluate(mapBasedVariableResolver3))
			.isEqualTo(Value.FALSE);

		MapBasedVariableResolver mapBasedValueResolver4 = VariableResolver.empty();
		Value<?> value5 = Value.of("Hello");
		MapBasedVariableResolver mapBasedValueResolver5 = mapBasedValueResolver4.with("a", Evaluated.value(value5));
		Value<?> value4 = Value.of("Goodbye");
		MapBasedVariableResolver mapBasedVariableResolver2 = mapBasedValueResolver5.with("b", Evaluated.value(value4));
		assertThat(expression.evaluate(mapBasedVariableResolver2))
			.isEqualTo(Value.TRUE);

        MapBasedVariableResolver mapBasedValueResolver2 = VariableResolver.empty();
		Value<?> value3 = Value.of(true);
		MapBasedVariableResolver mapBasedValueResolver3 = mapBasedValueResolver2.with("a", Evaluated.value(value3));
		Value<?> value2 = Value.of(true);
		MapBasedVariableResolver mapBasedVariableResolver1 = mapBasedValueResolver3.with("b", Evaluated.value(value2));
		assertThat(expression.evaluate(mapBasedVariableResolver1)).isEqualTo(Value.FALSE);

        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Value<?> value1 = Value.of(false);
		MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Evaluated.value(value1));
		Value<?> value = Value.of(true);
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver1.with("b", Evaluated.value(value));
		assertThat(expression.evaluate(mapBasedVariableResolver)).isEqualTo(Value.TRUE);
	}

	@Test
	void testInfixNotEqualsArrays() throws EvaluationException, ParseException {
		Expression expression = Defaults.expressionFactory().parse("a!=b");

		MapBasedVariableResolver mapBasedValueResolver2 = VariableResolver.empty();
		Value<?> value3 = Value.of(Value::of, Arrays.asList("a", "b", "c"));
		MapBasedVariableResolver mapBasedValueResolver3 = mapBasedValueResolver2.with("a", Evaluated.value(value3));
		Value<?> value2 = Value.of(Value::of, Arrays.asList("a", "b", "c"));
		assertThat(expression.evaluate(mapBasedValueResolver3.with("b", Evaluated.value(value2))))
			.isEqualTo(Value.FALSE);

		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Value<?> value1 = Value.of(Value::of, Arrays.asList("a", "b", "c"));
		MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Evaluated.value(value1));
		Value<?> value = Value.of(Value::of, Arrays.asList("c", "b", "a"));
		assertThat(
			expression.evaluate(mapBasedValueResolver1.with("b", Evaluated.value(value))))
			.isEqualTo(Value.TRUE);
	}

	@Test
	void testInfixNotEqualsStructures() throws EvaluationException, ParseException {
		Expression expression = Defaults.expressionFactory().parse("a!=b");

		Map<String, BigDecimal> structure1 =
			new HashMap<String, BigDecimal>() {
				{
					put("a", new BigDecimal(35));
					put("b", new BigDecimal(99));
				}
			};

		Map<String, BigDecimal> structure2 =
			new HashMap<String, BigDecimal>() {
				{
					put("a", new BigDecimal(35));
					put("b", new BigDecimal(99));
				}
			};

		Map<String, BigDecimal> structure3 =
			new HashMap<String, BigDecimal>() {
				{
					put("a", new BigDecimal(45));
					put("b", new BigDecimal(99));
				}
			};

		MapBasedVariableResolver mapBasedValueResolver2 = VariableResolver.empty();
		Value<?> value3 = Value.of(Value::of, structure1);
		MapBasedVariableResolver mapBasedValueResolver3 = mapBasedValueResolver2.with("a", Evaluated.value(value3));
		Value<?> value2 = Value.of(Value::of, structure2);
		assertThat(expression.evaluate(mapBasedValueResolver3.with("b", Evaluated.value(value2))))
			.isEqualTo(Value.FALSE);

		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Value<?> value1 = Value.of(Value::of, structure1);
		MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Evaluated.value(value1));
		Value<?> value = Value.of(Value::of, structure3);
		assertThat(expression.evaluate(mapBasedValueResolver1.with("b", Evaluated.value(value))))
			.isEqualTo(Value.TRUE);
	}
}
