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

import de.flapdoodle.eval.config.TestConfigurationProvider;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.ParseException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.data.Percentage;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class BaseEvaluationTest {

	protected void assertExpressionHasExpectedResult(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertThat(
			TestConfigurationProvider.StandardFactoryWithAdditionalTestOperators.parse(expression)
				.evaluate(ValueResolver.empty())
				.wrapped().toString()
		).isEqualTo(expectedResult);
	}

	protected void assertExpressionHasExpectedResult(String expression, Value<?> expectedResult)
		throws EvaluationException, ParseException {
		assertThat(
			TestConfigurationProvider.StandardFactoryWithAdditionalTestOperators.parse(expression)
				.evaluate(ValueResolver.empty())
		).isEqualTo(expectedResult);
	}

	protected void assertExpressionHasExpectedResult(String expression, Value.NumberValue expectedResult)
		throws EvaluationException, ParseException {
		assertThat(
			TestConfigurationProvider.StandardFactoryWithAdditionalTestOperators.parse(expression)
				.evaluate(ValueResolver.empty()))
			.isInstanceOf(Value.NumberValue.class)
			.extracting(Value::wrapped, InstanceOfAssertFactories.BIG_DECIMAL)
			.isCloseTo(expectedResult.wrapped(), Percentage.withPercentage(0.99999));
	}

	protected void assertExpressionHasExpectedResult(
		String expression, String expectedResult, ExpressionFactory expressionFactory)
		throws EvaluationException, ParseException {
		assertThat(evaluate(expression, expressionFactory).wrapped().toString())
			.isEqualTo(expectedResult);
	}

	protected void assertExpressionThrowsException(
		String expression, String message, ExpressionFactory factory) {
		assertThatThrownBy(() -> evaluate(expression, factory)).hasMessage(message);
	}

	private Value<?> evaluate(String expressionString, ExpressionFactory factory)
		throws EvaluationException, ParseException {
		ParsedExpression expression = factory.parse(expressionString);

		return expression.evaluate(ValueResolver.empty());
	}

	protected static Value.NumberValue numberValueOf(String doubleAsString) {
		return Value.of(new BigDecimal(doubleAsString));
	}
}
