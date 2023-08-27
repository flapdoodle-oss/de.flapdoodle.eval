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

import de.flapdoodle.eval.CommonToken;
import de.flapdoodle.eval.config.TestConfigurationProvider.DummyFunction;
import org.junit.jupiter.api.Test;

class TokenizerFunctionsTest extends BaseParserTest {

	@Test
	void testSimple() throws ParseException {
		configuration = configuration.withFunction("f", new DummyFunction());
		assertAllTokensParsedCorrectly(
			"f(x)",
			CommonToken.of(0, "f", TokenType.FUNCTION),
			CommonToken.of(1, "(", TokenType.BRACE_OPEN),
			CommonToken.of(2, "x", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(3, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testBlanks() throws ParseException {
		configuration = configuration.withFunction("f", new DummyFunction());
		assertAllTokensParsedCorrectly(
			"f (x)",
			CommonToken.of(0, "f", TokenType.FUNCTION),
			CommonToken.of(2, "(", TokenType.BRACE_OPEN),
			CommonToken.of(3, "x", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(4, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testUnderscores() throws ParseException {
		configuration = configuration.withFunction("_f_x_", new DummyFunction());
		assertAllTokensParsedCorrectly(
			"_f_x_(x)",
			CommonToken.of(0, "_f_x_", TokenType.FUNCTION),
			CommonToken.of(5, "(", TokenType.BRACE_OPEN),
			CommonToken.of(6, "x", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(7, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testWithNumbers() throws ParseException {
		configuration = configuration.withFunction("f1x2", new DummyFunction());
		assertAllTokensParsedCorrectly(
			"f1x2(x)",
			CommonToken.of(0, "f1x2", TokenType.FUNCTION),
			CommonToken.of(4, "(", TokenType.BRACE_OPEN),
			CommonToken.of(5, "x", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(6, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testWithMoreParameters() throws ParseException {
		assertAllTokensParsedCorrectly(
			"sum(a, 2, 3)",
			CommonToken.of(0, "sum", TokenType.FUNCTION),
			CommonToken.of(3, "(", TokenType.BRACE_OPEN),
			CommonToken.of(4, "a", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(5, ",", TokenType.COMMA),
			CommonToken.of(7, "2", TokenType.NUMBER_LITERAL),
			CommonToken.of(8, ",", TokenType.COMMA),
			CommonToken.of(10, "3", TokenType.NUMBER_LITERAL),
			CommonToken.of(11, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testWithMixedParameters() throws ParseException {
		assertAllTokensParsedCorrectly(
			"TEST(a, \"hello\", 3)",
			CommonToken.of(0, "TEST", TokenType.FUNCTION),
			CommonToken.of(4, "(", TokenType.BRACE_OPEN),
			CommonToken.of(5, "a", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(6, ",", TokenType.COMMA),
			CommonToken.of(8, "hello", TokenType.STRING_LITERAL),
			CommonToken.of(15, ",", TokenType.COMMA),
			CommonToken.of(17, "3", TokenType.NUMBER_LITERAL),
			CommonToken.of(18, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testFunctionInParameter() throws ParseException {
		assertAllTokensParsedCorrectly(
			"TEST(a, factorial(x), 3)",
			CommonToken.of(0, "TEST", TokenType.FUNCTION),
			CommonToken.of(4, "(", TokenType.BRACE_OPEN),
			CommonToken.of(5, "a", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(6, ",", TokenType.COMMA),
			CommonToken.of(8, "factorial", TokenType.FUNCTION),
			CommonToken.of(17, "(", TokenType.BRACE_OPEN),
			CommonToken.of(18, "x", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(19, ")", TokenType.BRACE_CLOSE),
			CommonToken.of(20, ",", TokenType.COMMA),
			CommonToken.of(22, "3", TokenType.NUMBER_LITERAL),
			CommonToken.of(23, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testFunctionInParameterInFunctionParameter() throws ParseException {
		assertAllTokensParsedCorrectly(
			"sum(a,factorial(min(x,y)),3)",
			CommonToken.of(0, "sum", TokenType.FUNCTION),
			CommonToken.of(3, "(", TokenType.BRACE_OPEN),
			CommonToken.of(4, "a", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(5, ",", TokenType.COMMA),
			CommonToken.of(6, "factorial", TokenType.FUNCTION),
			CommonToken.of(15, "(", TokenType.BRACE_OPEN),
			CommonToken.of(16, "min", TokenType.FUNCTION),
			CommonToken.of(19, "(", TokenType.BRACE_OPEN),
			CommonToken.of(20, "x", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(21, ",", TokenType.COMMA),
			CommonToken.of(22, "y", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(23, ")", TokenType.BRACE_CLOSE),
			CommonToken.of(24, ")", TokenType.BRACE_CLOSE),
			CommonToken.of(25, ",", TokenType.COMMA),
			CommonToken.of(26, "3", TokenType.NUMBER_LITERAL),
			CommonToken.of(27, ")", TokenType.BRACE_CLOSE));
	}
}
