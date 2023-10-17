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

import de.flapdoodle.eval.*;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.ParseException;
import de.flapdoodle.eval.values.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EqualsTest extends BaseEvaluationTest {

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"1=1 : true",
			"1==1 : true",
			"0=0 : true",
			"1=0 : false",
			"0=1 : false",
			"21.678=21.678 : true",
			"\"abc\"=\"abc\" : true",
			"\"abc\"=\"xyz\" : false",
			"1+2=4-1 : true",
			"-5.2=-5.2 : true",
			"localDateTime(2022,10,30)=localDateTime(2022,10,30) : true",
			"localDateTime(2022,10,30)=localDateTime(2022,10,01) : false",
			"parseDuration(\"PT24H\")=parseDuration(\"P1D\") : true",
		})
	void testInfixEqualsLiterals(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asBoolean(expectedResult));
	}

	@Test
	void testInfixEqualsVariables() throws EvaluationException, ParseException {
		Expression expression = ExpressionFactory.defaults().parse("a=b");

		MapBasedVariableResolver mapBasedValueResolver8 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver9 = mapBasedValueResolver8.with("a", Value.of(new BigDecimal("1.4")));
		assertThat(
			expression.evaluate(mapBasedValueResolver9.with("b", Value.of(new BigDecimal("1.4"))))
				)
			.isEqualTo(Value.TRUE);

		MapBasedVariableResolver mapBasedValueResolver6 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver7 = mapBasedValueResolver6.with("a", Value.of("Hello"));
		assertThat(expression.evaluate(mapBasedValueResolver7.with("b", Value.of("Hello")))
			)
			.isEqualTo(Value.TRUE);

		MapBasedVariableResolver mapBasedValueResolver4 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver5 = mapBasedValueResolver4.with("a", Value.of("Hello"));
		assertThat(expression.evaluate(mapBasedValueResolver5.with("b", Value.of("Goodbye")))
			)
			.isEqualTo(Value.FALSE);

        MapBasedVariableResolver mapBasedValueResolver2 = VariableResolver.empty();
        MapBasedVariableResolver mapBasedValueResolver3 = mapBasedValueResolver2.with("a", Value.of(true));
        assertThat(expression.evaluate(mapBasedValueResolver3.with("b", Value.of(true)))
			)
			.isEqualTo(Value.TRUE);

        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
        MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Value.of(false));
        assertThat(expression.evaluate(mapBasedValueResolver1.with("b", Value.of(true)))
			)
			.isEqualTo(Value.FALSE);
	}

	@Test
	void testInfixEqualsArrays() throws EvaluationException, ParseException {
		Expression expression = ExpressionFactory.defaults().parse("a=b");

		MapBasedVariableResolver mapBasedValueResolver2 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver3 = mapBasedValueResolver2.with("a", Value.of(Value::of, Arrays.asList("a", "b", "c")));
		assertThat(
			expression.evaluate(mapBasedValueResolver3.with("b", Value.of(Value::of, Arrays.asList("a", "b", "c"))))
				)
			.isEqualTo(Value.TRUE);

		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Value.of(Value::of, Arrays.asList("a", "b", "c")));
		assertThat(
			expression.evaluate(mapBasedValueResolver1.with("b", Value.of(Value::of, Arrays.asList("c", "b", "a"))))
				)
			.isEqualTo(Value.FALSE);
	}

	@Test
	void testInfixEqualsStructures() throws EvaluationException, ParseException {
		Expression expression = ExpressionFactory.defaults().parse("a=b");

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
		MapBasedVariableResolver mapBasedValueResolver3 = mapBasedValueResolver2.with("a", Value.of(Value::of, structure1));
		assertThat(expression.evaluate(mapBasedValueResolver3.with("b", Value.of(Value::of, structure2)))
			)
			.isEqualTo(Value.TRUE);

		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Value.of(Value::of, structure1));
		assertThat(expression.evaluate(mapBasedValueResolver1.with("b", Value.of(Value::of, structure3)))
			)
			.isEqualTo(Value.FALSE);
	}
}
