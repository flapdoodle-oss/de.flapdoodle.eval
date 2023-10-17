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
package de.flapdoodle.eval.evaluables.booleans;

import de.flapdoodle.eval.BaseEvaluationTest;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.ExpressionFactory;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.ParseException;
import de.flapdoodle.eval.values.MapBasedValueResolver;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
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
		Expression expression = ExpressionFactory.defaults().parse("a!=b");

		MapBasedValueResolver mapBasedValueResolver8 = ValueResolver.empty();
		MapBasedValueResolver mapBasedValueResolver9 = mapBasedValueResolver8.with("a", Value.of(new BigDecimal("1.4")));
		MapBasedValueResolver mapBasedVariableResolver4 = mapBasedValueResolver9.with("b", Value.of(new BigDecimal("1.4")));
		assertThat(
			expression.evaluate(mapBasedVariableResolver4))
			.isEqualTo(Value.FALSE);

		MapBasedValueResolver mapBasedValueResolver6 = ValueResolver.empty();
		MapBasedValueResolver mapBasedValueResolver7 = mapBasedValueResolver6.with("a", Value.of("Hello"));
		MapBasedValueResolver mapBasedVariableResolver3 = mapBasedValueResolver7.with("b", Value.of("Hello"));
		assertThat(expression.evaluate(mapBasedVariableResolver3))
			.isEqualTo(Value.FALSE);

		MapBasedValueResolver mapBasedValueResolver4 = ValueResolver.empty();
		MapBasedValueResolver mapBasedValueResolver5 = mapBasedValueResolver4.with("a", Value.of("Hello"));
		MapBasedValueResolver mapBasedVariableResolver2 = mapBasedValueResolver5.with("b", Value.of("Goodbye"));
		assertThat(expression.evaluate(mapBasedVariableResolver2))
			.isEqualTo(Value.TRUE);

        MapBasedValueResolver mapBasedValueResolver2 = ValueResolver.empty();
        MapBasedValueResolver mapBasedValueResolver3 = mapBasedValueResolver2.with("a", Value.of(true));
        MapBasedValueResolver mapBasedVariableResolver1 = mapBasedValueResolver3.with("b", Value.of(true));
		assertThat(expression.evaluate(mapBasedVariableResolver1)).isEqualTo(Value.FALSE);

        MapBasedValueResolver mapBasedValueResolver = ValueResolver.empty();
        MapBasedValueResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Value.of(false));
        MapBasedValueResolver mapBasedVariableResolver = mapBasedValueResolver1.with("b", Value.of(true));
		assertThat(expression.evaluate(mapBasedVariableResolver)).isEqualTo(Value.TRUE);
	}

	@Test
	void testInfixNotEqualsArrays() throws EvaluationException, ParseException {
		Expression expression = ExpressionFactory.defaults().parse("a!=b");

		MapBasedValueResolver mapBasedValueResolver2 = ValueResolver.empty();
		MapBasedValueResolver mapBasedValueResolver3 = mapBasedValueResolver2.with("a", Value.of(Value::of, Arrays.asList("a", "b", "c")));
		assertThat(expression.evaluate(mapBasedValueResolver3.with("b", Value.of(Value::of, Arrays.asList("a", "b", "c")))))
			.isEqualTo(Value.FALSE);

		MapBasedValueResolver mapBasedValueResolver = ValueResolver.empty();
		MapBasedValueResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Value.of(Value::of, Arrays.asList("a", "b", "c")));
		assertThat(
			expression.evaluate(mapBasedValueResolver1.with("b", Value.of(Value::of, Arrays.asList("c", "b", "a")))))
			.isEqualTo(Value.TRUE);
	}

	@Test
	void testInfixNotEqualsStructures() throws EvaluationException, ParseException {
		Expression expression = ExpressionFactory.defaults().parse("a!=b");

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

		MapBasedValueResolver mapBasedValueResolver2 = ValueResolver.empty();
		MapBasedValueResolver mapBasedValueResolver3 = mapBasedValueResolver2.with("a", Value.of(Value::of, structure1));
		assertThat(expression.evaluate(mapBasedValueResolver3.with("b", Value.of(Value::of, structure2))))
			.isEqualTo(Value.FALSE);

		MapBasedValueResolver mapBasedValueResolver = ValueResolver.empty();
		MapBasedValueResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Value.of(Value::of, structure1));
		assertThat(expression.evaluate(mapBasedValueResolver1.with("b", Value.of(Value::of, structure3))))
			.isEqualTo(Value.TRUE);
	}
}
