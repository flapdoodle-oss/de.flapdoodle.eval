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

import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.ParseException;
import de.flapdoodle.eval.values.MapBasedValueResolver;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionEvaluatorNullTest extends BaseExpressionEvaluatorTest {

	@Test
	void testNullEquals() throws ParseException, EvaluationException {
		Expression expression = createExpression("a == null");
		MapBasedValueResolver mapBasedValueResolver1 = ValueResolver.empty();
		MapBasedValueResolver mapBasedVariableResolver1 = mapBasedValueResolver1.with("a", Value.ofNull());
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver1, "true");
        MapBasedValueResolver mapBasedValueResolver = ValueResolver.empty();
        MapBasedValueResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Value.of(99));
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver, "false");
	}

	@Test
	void testNullNotEquals() throws ParseException, EvaluationException {
		Expression expression = createExpression("a != null");
		MapBasedValueResolver mapBasedValueResolver1 = ValueResolver.empty();
		MapBasedValueResolver mapBasedVariableResolver1 = mapBasedValueResolver1.with("a", Value.ofNull());
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver1, "false");
        MapBasedValueResolver mapBasedValueResolver = ValueResolver.empty();
        MapBasedValueResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Value.of(99));
		assertExpressionHasExpectedResult(expression, mapBasedVariableResolver, "true");
	}

	@Test
	void testHandleWithIf() throws EvaluationException, ParseException {
		Expression expression1 = createExpression("if(a != null, a * 5, 1)");
		MapBasedValueResolver mapBasedValueResolver3 = ValueResolver.empty();
		assertExpressionHasExpectedResult(expression1, mapBasedValueResolver3.with("a", Value.ofNull()), "1");
        MapBasedValueResolver mapBasedValueResolver = ValueResolver.empty();
        assertExpressionHasExpectedResult(expression1, mapBasedValueResolver.with("a", Value.of(3)), "15.0");

		Expression expression2 =
			createExpression("if(a == null, \"Unknown name\", \"The name is \" + a)");
		MapBasedValueResolver mapBasedValueResolver2 = ValueResolver.empty();
		assertExpressionHasExpectedResult(expression2, mapBasedValueResolver2.with("a", Value.ofNull()), "Unknown name");
		MapBasedValueResolver mapBasedValueResolver1 = ValueResolver.empty();
		assertExpressionHasExpectedResult(expression2, mapBasedValueResolver1.with("a", Value.of("Max")), "The name is Max");
	}

	@Test
	void testHandleWithIfFailCase() throws EvaluationException, ParseException {
		assertThatThrownBy(() -> {
			MapBasedValueResolver mapBasedValueResolver = ValueResolver.empty();
			evaluate("if(a == null, a * 5, 1)", mapBasedValueResolver.with("a", Value.ofNull()));
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessageContaining("wrong type");

		Expression expression2 =
			createExpression("if(a != null, \"Unknown name\", \"The name is \" + a)");
		MapBasedValueResolver mapBasedValueResolver = ValueResolver.empty();
		assertExpressionHasExpectedResult(expression2, mapBasedValueResolver.with("a", Value.ofNull()), "The name is null");
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
			MapBasedValueResolver mapBasedValueResolver = ValueResolver.empty();
			MapBasedValueResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Value.ofNull());
			evaluate("a * 5", mapBasedVariableResolver);
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessageContaining("wrong type");

		assertThatThrownBy(() -> {
			MapBasedValueResolver mapBasedValueResolver = ValueResolver.empty();
			MapBasedValueResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Value.ofNull());
			evaluate("floor(a)", mapBasedVariableResolver);
		}).isInstanceOf(EvaluationException.class);

		assertThatThrownBy(() -> {
			MapBasedValueResolver mapBasedValueResolver = ValueResolver.empty();
			MapBasedValueResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Value.ofNull());
			evaluate("a > 5", mapBasedVariableResolver);
		}).isInstanceOf(EvaluationException.class);
	}

	private void assertExpressionHasExpectedResult(Expression expression, ValueResolver variableResolver, String expectedResult)
		throws EvaluationException, ParseException {
		assertThat(expression.evaluate(variableResolver).toString()).isEqualTo(expectedResult);
	}
}
