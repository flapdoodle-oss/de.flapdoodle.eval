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
package de.flapdoodle.eval.operators.booleans;

import de.flapdoodle.eval.*;
import de.flapdoodle.eval.config.MapBasedValueResolver;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.ParseException;
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
		assertExpressionHasExpectedResult(expression, expectedResult);
	}

	@Test
	void testInfixNotEqualsVariables() throws EvaluationException, ParseException {
		ParsedExpression expression = ExpressionFactory.defaults().parse("a!=b");

		MapBasedValueResolver mapBasedVariableResolver4 = ValueResolver.empty()
			.with("a", new BigDecimal("1.4"))
			.with("b", new BigDecimal("1.4"));
		assertThat(
			expression.evaluate(mapBasedVariableResolver4)
				.wrapped())
			.isEqualTo(false);

		MapBasedValueResolver mapBasedVariableResolver3 = ValueResolver.empty()
			.with("a", "Hello")
			.with("b", "Hello");
		assertThat(expression.evaluate(mapBasedVariableResolver3).wrapped())
			.isEqualTo(false);

		MapBasedValueResolver mapBasedVariableResolver2 = ValueResolver.empty()
			.with("a", "Hello").with("b", "Goodbye");
		assertThat(expression.evaluate(mapBasedVariableResolver2).wrapped())
			.isEqualTo(true);

		MapBasedValueResolver mapBasedVariableResolver1 = ValueResolver.empty()
			.with("a", true).with("b", true);
		assertThat(expression.evaluate(mapBasedVariableResolver1).wrapped()).isEqualTo(false);

		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("a", false).with("b", true);
		assertThat(expression.evaluate(mapBasedVariableResolver).wrapped()).isEqualTo(true);
	}

	@Test
	void testInfixNotEqualsArrays() throws EvaluationException, ParseException {
		ParsedExpression expression = ExpressionFactory.defaults().parse("a!=b");

		MapBasedValueResolver mapBasedVariableResolver1 = ValueResolver.empty()
			.with("a", Value::of, Arrays.asList("a", "b", "c"))
			.with("b", Value::of, Arrays.asList("a", "b", "c"));
		assertThat(
			expression.evaluate(mapBasedVariableResolver1)
				.wrapped())
			.isEqualTo(false);

		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("a", Value::of, Arrays.asList("a", "b", "c"))
			.with("b", Value::of, Arrays.asList("c", "b", "a"));
		assertThat(
			expression.evaluate(mapBasedVariableResolver)
				.wrapped())
			.isEqualTo(true);
	}

	@Test
	void testInfixNotEqualsStructures() throws EvaluationException, ParseException {
		ParsedExpression expression = ExpressionFactory.defaults().parse("a!=b");

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

		MapBasedValueResolver mapBasedVariableResolver1 = ValueResolver.empty()
			.with("a", Value::of, structure1).with("b", Value::of, structure2);
		assertThat(expression.evaluate(mapBasedVariableResolver1).wrapped())
			.isEqualTo(false);

		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("a", Value::of, structure1).with("b", Value::of, structure3);
		assertThat(expression.evaluate(mapBasedVariableResolver).wrapped())
			.isEqualTo(true);
	}
}
