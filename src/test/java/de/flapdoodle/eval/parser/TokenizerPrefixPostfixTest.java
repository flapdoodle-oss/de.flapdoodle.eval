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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenizerPrefixPostfixTest extends BaseParserTest {

	@Test
	void testPrefixSingle() throws ParseException {
		assertAllTokensParsedCorrectly(
			"++a",
			Token.of(1, "++", TokenType.PREFIX_OPERATOR),
			Token.of(3, "a", TokenType.VARIABLE_OR_CONSTANT));
	}

	@Test
	void testPostfixSingle() throws ParseException {
		assertAllTokensParsedCorrectly(
			"a++",
			Token.of(1, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(2, "++", TokenType.POSTFIX_OPERATOR));
	}

	@Test
	void testPostfixAsPrefixThrowsException() {
		assertThatThrownBy(new Tokenizer("?a", configuration)::parse)
			.isInstanceOf(ParseException.class)
			.hasMessage("Undefined operator '?'");
	}

	@Test
	void testPrefixAndPostfix() throws ParseException {
		// note: if this is supported, depends on the operator and what type it expects as operand
		assertAllTokensParsedCorrectly(
			"++a++",
			Token.of(1, "++", TokenType.PREFIX_OPERATOR),
			Token.of(3, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(4, "++", TokenType.POSTFIX_OPERATOR));
	}

	@Test
	void testPrefixWithInfixAndPostfix() throws ParseException {
		assertAllTokensParsedCorrectly(
			"++a+a++",
			Token.of(1, "++", TokenType.PREFIX_OPERATOR),
			Token.of(3, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(4, "+", TokenType.INFIX_OPERATOR),
			Token.of(5, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(6, "++", TokenType.POSTFIX_OPERATOR));
	}

	@Test
	void testPrefixWithBraces() throws ParseException {
		assertAllTokensParsedCorrectly(
			"(++a)+(a++)",
			Token.of(1, "(", TokenType.BRACE_OPEN),
			Token.of(2, "++", TokenType.PREFIX_OPERATOR),
			Token.of(4, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(5, ")", TokenType.BRACE_CLOSE),
			Token.of(6, "+", TokenType.INFIX_OPERATOR),
			Token.of(7, "(", TokenType.BRACE_OPEN),
			Token.of(8, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(9, "++", TokenType.POSTFIX_OPERATOR),
			Token.of(11, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testPrefixWithFunction() throws ParseException {
		assertAllTokensParsedCorrectly(
			"++max(++a,a++,b++)++",
			Token.of(1, "++", TokenType.PREFIX_OPERATOR),
			Token.of(3, "max", TokenType.FUNCTION),
			Token.of(6, "(", TokenType.BRACE_OPEN),
			Token.of(7, "++", TokenType.PREFIX_OPERATOR),
			Token.of(9, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(10, ",", TokenType.COMMA),
			Token.of(11, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(12, "++", TokenType.POSTFIX_OPERATOR),
			Token.of(14, ",", TokenType.COMMA),
			Token.of(15, "b", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(16, "++", TokenType.POSTFIX_OPERATOR),
			Token.of(18, ")", TokenType.BRACE_CLOSE),
			Token.of(19, "++", TokenType.POSTFIX_OPERATOR));
	}

	@Test
	void testPrefixWithStringLiteral() throws ParseException {
		assertAllTokensParsedCorrectly(
			"++\"hello\"++",
			Token.of(1, "++", TokenType.PREFIX_OPERATOR),
			Token.of(3, "hello", TokenType.STRING_LITERAL),
			Token.of(10, "++", TokenType.POSTFIX_OPERATOR));
	}

	@Test
	void testPrefixWithUnaryAndBinary() throws ParseException {
		assertAllTokensParsedCorrectly(
			"-a - -b++",
			Token.of(1, "-", TokenType.PREFIX_OPERATOR),
			Token.of(2, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(4, "-", TokenType.INFIX_OPERATOR),
			Token.of(6, "-", TokenType.PREFIX_OPERATOR),
			Token.of(7, "b", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(8, "++", TokenType.POSTFIX_OPERATOR));
	}
}
