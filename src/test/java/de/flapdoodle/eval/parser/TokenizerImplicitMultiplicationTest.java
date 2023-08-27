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
import de.flapdoodle.eval.config.Configuration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenizerImplicitMultiplicationTest extends BaseParserTest {

	@Test
	void testImplicitBraces() throws ParseException {
		assertAllTokensParsedCorrectly(
			"(a+b)(a-b)",
			CommonToken.of(0, "(", TokenType.BRACE_OPEN),
			CommonToken.of(1, "a", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(2, "+", TokenType.INFIX_OPERATOR),
			CommonToken.of(3, "b", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(4, ")", TokenType.BRACE_CLOSE),
			CommonToken.of(5, "*", TokenType.INFIX_OPERATOR),
			CommonToken.of(5, "(", TokenType.BRACE_OPEN),
			CommonToken.of(6, "a", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(7, "-", TokenType.INFIX_OPERATOR),
			CommonToken.of(8, "b", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(9, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testImplicitNumberBraces() throws ParseException {
		assertAllTokensParsedCorrectly(
			"2(x)",
			CommonToken.of(0, "2", TokenType.NUMBER_LITERAL),
			CommonToken.of(1, "*", TokenType.INFIX_OPERATOR),
			CommonToken.of(1, "(", TokenType.BRACE_OPEN),
			CommonToken.of(2, "x", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(3, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testImplicitNumberNoBraces() throws ParseException {
		assertAllTokensParsedCorrectly(
			"2x",
			CommonToken.of(0, "2", TokenType.NUMBER_LITERAL),
			CommonToken.of(1, "*", TokenType.INFIX_OPERATOR),
			CommonToken.of(1, "x", TokenType.VARIABLE_OR_CONSTANT));
	}

	@Test
	void testImplicitNumberVariable() throws ParseException {
		assertAllTokensParsedCorrectly(
			"2x",
			CommonToken.of(0, "2", TokenType.NUMBER_LITERAL),
			CommonToken.of(1, "*", TokenType.INFIX_OPERATOR),
			CommonToken.of(1, "x", TokenType.VARIABLE_OR_CONSTANT));
	}

	@Test
	void testImplicitMultiplicationNotAllowed() {
		Configuration config =
			Configuration.builder().isImplicitMultiplicationAllowed(false).build();

		assertThatThrownBy(() -> new Tokenizer("2(x+y)", config).parse())
			.isEqualTo(new ParseException(1, 1, "(", "Missing operator"));
	}
}
