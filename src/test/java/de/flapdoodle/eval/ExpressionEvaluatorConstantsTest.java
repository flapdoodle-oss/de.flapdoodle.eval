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

import de.flapdoodle.eval.core.Expression;
import de.flapdoodle.eval.core.ExpressionFactory;
import de.flapdoodle.eval.core.MapBasedVariableResolver;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.ParseException;
import de.flapdoodle.eval.example.Defaults;
import de.flapdoodle.eval.example.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ExpressionEvaluatorConstantsTest extends BaseExpressionEvaluatorTest {

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"true : true",
			"false : false",
			"PI : "
				+ " 3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679",
			"E : 2.71828182845904523536028747135266249775724709369995957496696762772407663",
		})
	void testDefaultConstants(String expression, String expectedResult)
		throws ParseException, EvaluationException {
		assertThat(evaluate(expression)).isEqualTo(expectedResult);
	}

	@Test
	void testCustomConstantsMixedCase() throws EvaluationException, ParseException {
		Map<String, Value<?>> constants =
			new HashMap<String, Value<?>>() {
				{
					put("a", Value.of(new BigDecimal("2.5")));
					put("B", Value.of(new BigDecimal("3.9")));
				}
			};

		MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().withValues(constants);
		ExpressionFactory factory = Defaults.expressionFactory()
				.withConstants(mapBasedVariableResolver);

		Expression expression = factory.parse("a+B");

		assertThat(expression.evaluate(VariableResolver.empty()).toString()).isEqualTo("6.4");
	}

	@Test
	void testOverwriteConstantsWith() throws EvaluationException, ParseException {
		assertThat(factory.withConstant("e", Value.of(9))
				.parse("e")
				.evaluate(VariableResolver.empty()).toString()).isEqualTo("9.0");
	}

	@Test
	void testOverwriteConstantsWithValues() throws EvaluationException, ParseException {
		MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().with("e", Value.of(3));
		assertThat(factory.withConstant("e", Value.of(6))
				.parse("e")
				.evaluate(mapBasedVariableResolver).toString()).isEqualTo("6.0");
	}
}
