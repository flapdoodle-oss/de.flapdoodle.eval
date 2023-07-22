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
package de.flapdoodle.eval.functions.basic;

import de.flapdoodle.eval.BaseEvaluationTest;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.config.Configuration;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.ParseException;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.parser.TokenType;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BasicFunctionsTest extends BaseEvaluationTest {

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"FACT(0) : 1",
			"FACT(1) : 1",
			"FACT(2) : 2",
			"FACT(3) : 6",
			"FACT(5) : 120",
			"FACT(10) : 3628800",
			"FACT(20) : 2432902008176640000"
		})
	void testFactorial(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"IF(true, 4/2, 4/0) : 2",
			"IF(true, 4/IF(false, 5/0, 2*2), 4/0) : 1",
			"IF(true, 6/IF(false, 5/0, 2*IF(true, 3, 6/0)), 4/0) : 1"
		})
	void testIf(String expression, String expectedResult) throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"MAX(99) : 99",
			"MAX(2,1) : 2",
			"MAX(1,9,-5,6,3,7) : 9",
			"MAX(17,88,77,66,609,1567,1876534) : 1876534"
		})
	void testMax(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"MIN(99) : 99",
			"MIN(2,1) : 1",
			"MIN(1,9,-5,6,3,7) : -5",
			"MIN(17,88,77,66,609,1567,1876534) : 17"
		})
	void testMin(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"ROUND(1.1,0) : 1",
			"ROUND(1.5,0) : 2",
			"ROUND(2.34,1) : 2.3",
			"ROUND(2.35,1) : 2.4",
			"ROUND(2.323789,2) : 2.32",
			"ROUND(2.324789,2) : 2.32"
		})
	void testRoundHalfEven(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"ROUND(1.1,0) : 2",
			"ROUND(1.5,0) : 2",
			"ROUND(2.34,1) : 2.4",
			"ROUND(2.35,1) : 2.4",
			"ROUND(2.323789,2) : 2.33",
			"ROUND(2.324789,2) : 2.33"
		})
	void testRoundUp(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		Configuration configuration =
			Configuration.builder().mathContext(new MathContext(32, RoundingMode.UP)).build();
		assertExpressionHasExpectedResult(expression, expectedResult, configuration);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"SUM(1) : 1",
			"SUM(1,2,3,4) : 10",
			"SUM(1,-1) : 0",
			"SUM(1,10,100,1000,10000) : 11111",
			"SUM(1,2,3,-3,-2,5) : 6"
		})
	void testSum(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"SQRT(0) : 0",
			"SQRT(1) : 1",
			"SQRT(2) : 1.41421356237309504880168872420969807856967187537694807317667973799073",
			"SQRT(4) : 2",
			"SQRT(5) : 2.23606797749978969640917366873127623544061835961152572427089724541052",
			"SQRT(10) : 3.16227766016837933199889354443271853371955513932521682685750485279259",
			"SQRT(365) : 19.10497317454280017916829575249669141539647233176799736525808213487",
			"SQRT(236769) : 486.58914907753543122473972072155030396245230523850016876894122736411182"
		})
	void testSqrt(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@Test
	void testSqrtNegative() {
		assertThatThrownBy(() -> Expression.of("SQRT(-1)").evaluate(ValueResolver.empty()))
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Parameter must not be negative");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
//        "NOT(0) : true",
//        "NOT(1) : false",
//        "NOT(20) : false",
//        "NOT(\"true\") : false",
//        "NOT(\"false\") : true",
//        "NOT(2-4/2) : true",
			"NOT(true) : false",
			"NOT(false) : true",
		})
	void testBooleanNegation(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult);
	}

	@Test
	void testNotFunctionDirectly() throws EvaluationException {
		Not not = new Not();
		Expression expressionMock = Expression.of("ignore");
		Token token = Token.of(1, "NOT", TokenType.FUNCTION, not);

		ValueResolver variableResolver = ValueResolver.empty();

		assertThat(
			not
				.evaluate(variableResolver, expressionMock, token, Arrays.asList(Value.of(true)))
				.wrapped())
			.asInstanceOf(InstanceOfAssertFactories.BOOLEAN)
			.isFalse();
		assertThat(
			not
				.evaluate(variableResolver, expressionMock, token, Arrays.asList(Value.of(false)))
				.wrapped())
			.asInstanceOf(InstanceOfAssertFactories.BOOLEAN)
			.isTrue();
	}

	@Test
	void testRandom() throws EvaluationException, ParseException {
		Expression expression1 = Expression.of("RANDOM()");
		Value<?> r1 = expression1.evaluate(ValueResolver.empty());
		Expression expression = Expression.of("RANDOM()");
		Value<?> r2 = expression.evaluate(ValueResolver.empty());

		assertThat(r1.wrapped()).isNotEqualTo(r2.wrapped());
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"ABS(0) : 0",
			"ABS(1) : 1",
			"ABS(-1) : 1",
			"ABS(20) : 20",
			"ABS(-20) : 20",
			"ABS(2.12345) : 2.12345",
			"ABS(-2.12345) : 2.12345"
		})
	void testAbs(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"FLOOR(0) : 0",
			"FLOOR(1) : 1",
			"FLOOR(-1) : -1",
			"FLOOR(20) : 20",
			"FLOOR(-20) : -20",
			"FLOOR(2.12345) : 2",
			"FLOOR(-2.12345) : -3",
			"FLOOR(-2.97345) : -3"
		})
	void testFloor(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"CEILING(0) : 0",
			"CEILING(1) : 1",
			"CEILING(-1) : -1",
			"CEILING(20) : 20",
			"CEILING(-20) : -20",
			"CEILING(2.12345) : 3",
			"CEILING(-2.12345) : -2",
			"CEILING(-2.97345) : -2"
		})
	void testCeiling(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, expectedResult);
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"LOG(1) : 0",
			"LOG(10) : 2.302585092994046",
			"LOG(2.12345) : 0.7530421244614831",
			"LOG(1567) : 7.356918242356021"
		})
	void testLog(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@Test
	void testLogNegative() {
		assertThatThrownBy(() -> Expression.of("LOG(-1)").evaluate(ValueResolver.empty()))
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Parameter must not be negative");
	}

	@Test
	void testLogZero() {
		assertThatThrownBy(() -> Expression.of("LOG(0)").evaluate(ValueResolver.empty()))
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Parameter must not be zero");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"LOG10(1) : 0",
			"LOG10(10) : 1",
			"LOG10(2.12345) : 0.3270420392943239",
			"LOG10(1567) : 3.1950689964685903"
		})
	void testLog10(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, numberValueOf(expectedResult));
	}

	@Test
	void testLog10Negative() {
		assertThatThrownBy(() -> Expression.of("LOG10(-1)").evaluate(ValueResolver.empty()))
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Parameter must not be negative");
	}

	@Test
	void testLog10Zero() {
		assertThatThrownBy(() -> Expression.of("LOG10(0)").evaluate(ValueResolver.empty()))
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Parameter must not be zero");
	}
}
