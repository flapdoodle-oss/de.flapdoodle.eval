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
/*
  Copyright 2012-2023 Udo Klimaschewski

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package de.flapdoodle.eval;

import de.flapdoodle.eval.config.MapBasedValueResolver;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionEvaluatorNullTest extends BaseExpressionEvaluatorTest {

	@Test
	void testNullEquals() throws ParseException, EvaluationException {
		Expression expression = createExpression("a == null");
		MapBasedValueResolver mapBasedVariableResolver1 = ValueResolver.empty().withNull("a");
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver1, "true");
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().with("a", 99);
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver, "false");
	}

	@Test
	void testNullNotEquals() throws ParseException, EvaluationException {
		Expression expression = Expression.of("a != null");
		MapBasedValueResolver mapBasedVariableResolver1 = ValueResolver.empty().withNull("a");
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver1, "false");
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().with("a", 99);
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver, "true");
	}

	@Test
	void testHandleWithIf() throws EvaluationException, ParseException {
		Expression expression1 = createExpression("IF(a != null, a * 5, 1)");
		MapBasedValueResolver mapBasedVariableResolver3 = ValueResolver.empty().withNull("a");
		assertExpressionHasExpectedResult(expression1, mapBasedVariableResolver3, "1");
		MapBasedValueResolver mapBasedVariableResolver2 = ValueResolver.empty().with("a", 3);
		assertExpressionHasExpectedResult(expression1, mapBasedVariableResolver2, "15.0");

		Expression expression2 =
			createExpression("IF(a == null, \"Unknown name\", \"The name is \" + a)");
		MapBasedValueResolver mapBasedVariableResolver1 = ValueResolver.empty().withNull("a");
		assertExpressionHasExpectedResult(expression2, mapBasedVariableResolver1, "Unknown name");
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().with("a", "Max");
		assertExpressionHasExpectedResult(expression2, mapBasedVariableResolver, "The name is Max");
	}

	@Test
	void testHandleWithMaps() throws EvaluationException, ParseException {
		Expression expression = createExpression("a == null && b == null");
		Map<String, Value<?>> values = new HashMap<>();
		values.put("a", Value.ofNull());
		values.put("b", Value.ofNull());

		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().withValues(values);
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver, "true");
	}

	@Test
	void testFailWithNoHandling() {
		assertThatThrownBy(() -> {
			MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().withNull("a");
			evaluate("a * 5", mapBasedVariableResolver);
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessageContaining("type missmatch");

		assertThatThrownBy(() -> {
			MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().withNull("a");
			evaluate("FLOOR(a)", mapBasedVariableResolver);
		}).isInstanceOf(EvaluationException.class);

		assertThatThrownBy(() -> {
			MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().withNull("a");
			evaluate("a > 5", mapBasedVariableResolver);
		}).isInstanceOf(EvaluationException.class);
	}

	private void assertExpressionHasExpectedResult(Expression expression, ValueResolver variableResolver, String expectedResult)
		throws EvaluationException, ParseException {
		assertThat(expression.evaluate(variableResolver).wrapped().toString()).isEqualTo(expectedResult);
	}
}
