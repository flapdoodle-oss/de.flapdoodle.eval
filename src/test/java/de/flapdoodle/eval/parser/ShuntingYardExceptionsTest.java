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
package de.flapdoodle.eval.parser;

import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.config.ValueResolver;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static de.flapdoodle.eval.parser.TokenType.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShuntingYardExceptionsTest extends BaseParserTest {

	static {
		Assertions.setMaxStackTraceElementsDisplayed(20);
	}

	@Test
	void testUnexpectedToken() {
		List<Token> tokens = Arrays.asList(Token.of(1, "x", FUNCTION_PARAM_START));
		assertThatThrownBy(new ShuntingYardConverter("x", tokens, operatorResolver, functionResolver)::toAbstractSyntaxTree)
			.isInstanceOf(ParseException.class)
			.hasMessage("Unexpected token of type 'FUNCTION_PARAM_START'");
	}

	@Test
	void testMissingPrefixOperand() {
		List<Token> tokens = Arrays.asList(Token.of(1, "-", PREFIX_OPERATOR));
		assertThatThrownBy(new ShuntingYardConverter("-", tokens, operatorResolver, functionResolver)::toAbstractSyntaxTree)
			.isInstanceOf(ParseException.class)
			.hasMessage("Missing operand for operator");
	}

	@Test
	void testMissingSecondInfixOperand() {
		List<Token> tokens =
			Arrays.asList(
				Token.of(1, "2", VARIABLE_OR_CONSTANT),
				Token.of(2, "*", INFIX_OPERATOR));
		assertThatThrownBy(new ShuntingYardConverter("2*", tokens, operatorResolver, functionResolver)::toAbstractSyntaxTree)
			.isInstanceOf(ParseException.class)
			.hasMessage("Missing second operand for operator");
	}

	@Test
	void testEmptyExpression() {
		Expression expression = Expression.of("");

		assertThatThrownBy(() -> expression.evaluate(ValueResolver.empty()))
			.isInstanceOf(ParseException.class)
			.hasMessage("Empty expression");
	}

	@Test
	void testEmptyExpressionBraces() {
		Expression expression = Expression.of("()");

		assertThatThrownBy(() -> expression.evaluate(ValueResolver.empty()))
			.isInstanceOf(ParseException.class)
			.hasMessage("Empty expression");
	}

	@Test
	void testComma() {
		Expression expression = Expression.of(",");

		assertThatThrownBy(() -> expression.evaluate(ValueResolver.empty()))
			.isInstanceOf(ParseException.class)
			.hasMessage("Empty expression");
	}

	@Test
	void testDoubleStructureOperator() {
		List<Token> tokens =
			Arrays.asList(Token.of(1, ".", STRUCTURE_SEPARATOR), Token.of(2, ".", STRUCTURE_SEPARATOR));
		assertThatThrownBy(new ShuntingYardConverter("..", tokens, operatorResolver, functionResolver)::toAbstractSyntaxTree)
			.isInstanceOf(ParseException.class)
			.hasMessage("Missing operand for operator");
	}

	@Test
	void testStructureFollowsPostfixOperator() {
		List<Token> tokens =
			Arrays.asList(Token.of(1, ".", STRUCTURE_SEPARATOR), Token.of(2, "!", POSTFIX_OPERATOR));
		assertThatThrownBy(new ShuntingYardConverter("..", tokens, operatorResolver, functionResolver)::toAbstractSyntaxTree)
			.isInstanceOf(ParseException.class)
			.hasMessage("Missing operand for operator");
	}

	@Test
	void testStructureFollowsTwoPostfixOperators() {
		List<Token> tokens =
			Arrays.asList(
				Token.of(1, ".", STRUCTURE_SEPARATOR),
				Token.of(2, "!", POSTFIX_OPERATOR),
				Token.of(2, "!", POSTFIX_OPERATOR));
		assertThatThrownBy(new ShuntingYardConverter("..", tokens, operatorResolver, functionResolver)::toAbstractSyntaxTree)
			.isInstanceOf(ParseException.class)
			.hasMessage("Missing operand for operator");
	}

	@Test
	void testFunctionNotEnoughParameters() {
		Expression expression = Expression.of("round(2)");

		assertThatThrownBy(() -> expression.evaluate(ValueResolver.empty()))
			.isInstanceOf(ParseException.class)
			.hasMessage("Not enough parameters for function");
	}

	@Test
	void testFunctionNotEnoughParametersForVarArgs() {
		Expression expression = Expression.of("min()");

		assertThatThrownBy(() -> expression.evaluate(ValueResolver.empty()))
			.isInstanceOf(ParseException.class)
			.hasMessage("Not enough parameters for function");
	}

	@Test
	void testFunctionTooManyParameters() {
		Expression expression = Expression.of("round(1,2,3)");

		assertThatThrownBy(() -> expression.evaluate(ValueResolver.empty()))
			.isInstanceOf(ParseException.class)
			.hasMessage("Too many parameters for function");
	}

	@ParameterizedTest
	@ValueSource(
		strings = {
			"Hello, World",
			"Hello round(1,2) + (1 + 1)",
			"Hello round(1,2)",
			"Hello 1 + (1 + 1)",
			"Hello 1 + 1",
			"Hello World",
			"Hello 1",
			"1 2",
			"e.1",
			"E.1"
		})
	void testTooManyOperands(String expressionString) {
		Expression expression = Expression.of(expressionString);

		assertThatThrownBy(() -> expression.evaluate(ValueResolver.empty()))
			.isInstanceOf(ParseException.class)
			.hasMessage("Too many operands");
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = { "(x+y)*(a-) : 9", "a** : 2", "5+, : 2" })
	void testInvalidTokenAfterInfixOperator(String expressionString, int position) {
		Expression expression = Expression.of(expressionString);

		assertThatThrownBy(() -> expression.evaluate(ValueResolver.empty()))
			.isInstanceOf(ParseException.class)
			.hasMessage("Unexpected token after infix operator")
			.extracting("startPosition")
			.isEqualTo(position);
	}
}
