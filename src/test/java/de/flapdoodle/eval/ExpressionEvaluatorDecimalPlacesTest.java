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
package de.flapdoodle.eval;

import de.flapdoodle.eval.config.MapBasedValueResolver;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ExpressionEvaluatorDecimalPlacesTest extends BaseExpressionEvaluatorTest {

	@Test
	void testDefaultNoRoundingLiteral() throws ParseException, EvaluationException {
		assertThat(evaluate("2.12345")).isEqualTo("2.12345");
	}

	@Test
	void testDefaultNoRoundingVariable() throws ParseException, EvaluationException {
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("a", Value.of(new BigDecimal("2.12345")));
		ValueResolver variableResolver = mapBasedVariableResolver;
		assertThat(evaluate("a", variableResolver)).isEqualTo("2.12345");
	}

	@Test
	void testDefaultNoRoundingInfixOperator() throws ParseException, EvaluationException {
		assertThat(evaluate("2.12345+1.54321")).isEqualTo("3.66666");
	}

	@Test
	void testDefaultNoRoundingPrefixOperator() throws ParseException, EvaluationException {
		assertThat(evaluate("-2.12345")).isEqualTo("-2.12345");
	}

	@Test
	void testDefaultNoRoundingFunction() throws ParseException, EvaluationException {
		assertThat(evaluate("sum(2.12345,1.54321)")).isEqualTo("3.66666");
	}

	@Test
	void testDefaultNoRoundingArray() throws ParseException, EvaluationException {
		List<BigDecimal> array = Arrays.asList(new BigDecimal("1.12345"));
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("a", Value::of, array);
		ValueResolver variableResolver = mapBasedVariableResolver;
		assertThat(evaluate("a[0]", variableResolver)).isEqualTo("1.12345");
	}

	@Test
	void testDefaultNoRoundingStructure() throws ParseException, EvaluationException {
		Map<String, BigDecimal> structure =
			new HashMap<String, BigDecimal>() {
				{
					put("b", new BigDecimal("1.12345"));
				}
			};

		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("a", Value::of, structure);
		ValueResolver variableResolver = mapBasedVariableResolver;
		assertThat(evaluate("a.b", variableResolver)).isEqualTo("1.12345");
	}
}
