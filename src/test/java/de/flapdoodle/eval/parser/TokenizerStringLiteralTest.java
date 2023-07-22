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
import de.flapdoodle.eval.config.MapBasedValueResolver;
import de.flapdoodle.eval.config.ValueResolver;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenizerStringLiteralTest extends BaseParserTest {

	@Test
	void testSimpleQuote() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\"Hello, World\"", Token.of(1, "Hello, World", TokenType.STRING_LITERAL));
	}

	@Test
	void testSimpleQuoteLeadingBlanks() throws ParseException {
		assertAllTokensParsedCorrectly(
			"  \t\n \"Hello, World\"", Token.of(6, "Hello, World", TokenType.STRING_LITERAL));
	}

	@Test
	void testSimpleQuoteTrailingBlanks() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\"Hello, World\"  \t\n ", Token.of(1, "Hello, World", TokenType.STRING_LITERAL));
	}

	@Test
	void testEscapeDoubleQuote() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\"Hello, \\\"World\\\"\"", Token.of(1, "Hello, \"World\"", TokenType.STRING_LITERAL));
	}

	@Test
	void testEscapeSingleQuote() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\"Hello, \\'World\\'\"", Token.of(1, "Hello, 'World'", TokenType.STRING_LITERAL));
	}

	@Test
	void testEscapeBackslash() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\"a \\\\ b\"", Token.of(1, "a \\ b", TokenType.STRING_LITERAL));
	}

	@Test
	void testEscapeCharacters() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\" \\t \\r \\n \\f \\b \"", Token.of(1, " \t \r \n \f \b ", TokenType.STRING_LITERAL));
	}

	@Test
	void testUnknownEscapeCharacter() {
		assertThatThrownBy(() -> {
			MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty();
			Expression.of("\" \\y \"").evaluate(mapBasedVariableResolver);
		})
			.isInstanceOf(ParseException.class)
			.hasMessage("Unknown escape character");
	}

	@Test
	void testSimpleQuoteOperation() throws ParseException {
		assertAllTokensParsedCorrectly(
			"\"Hello\" + \" \" + \"World\"",
			Token.of(1, "Hello", TokenType.STRING_LITERAL),
			Token.of(9, "+", TokenType.INFIX_OPERATOR),
			Token.of(11, " ", TokenType.STRING_LITERAL),
			Token.of(15, "+", TokenType.INFIX_OPERATOR),
			Token.of(17, "World", TokenType.STRING_LITERAL));
	}

	@Test
	void testErrorUnmatchedQuoteStart() {
		assertThatThrownBy(() -> new Tokenizer("\"hello", configuration).parse())
			.isInstanceOf(ParseException.class)
			.hasMessage("Closing quote not found");
	}

	@Test
	void testErrorUnmatchedQuoteOffset() {
		assertThatThrownBy(() -> new Tokenizer("test \"hello", configuration).parse())
			.isInstanceOf(ParseException.class)
			.hasMessage("Closing quote not found");
	}
}
