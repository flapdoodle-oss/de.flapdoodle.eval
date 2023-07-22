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

import de.flapdoodle.eval.config.Configuration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenizerArrayTest extends BaseParserTest {

	@Test
	void testArraySimple() throws ParseException {
		assertAllTokensParsedCorrectly(
			"a[1]",
			Token.of(1, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(2, "[", TokenType.ARRAY_OPEN),
			Token.of(3, "1", TokenType.NUMBER_LITERAL),
			Token.of(4, "]", TokenType.ARRAY_CLOSE));
	}

	@Test
	void testArrayNested() throws ParseException {
		assertAllTokensParsedCorrectly(
			"a[b[2] + c[3]]",
			Token.of(1, "a", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(2, "[", TokenType.ARRAY_OPEN),
			Token.of(3, "b", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(4, "[", TokenType.ARRAY_OPEN),
			Token.of(5, "2", TokenType.NUMBER_LITERAL),
			Token.of(6, "]", TokenType.ARRAY_CLOSE),
			Token.of(8, "+", TokenType.INFIX_OPERATOR),
			Token.of(10, "c", TokenType.VARIABLE_OR_CONSTANT),
			Token.of(11, "[", TokenType.ARRAY_OPEN),
			Token.of(12, "3", TokenType.NUMBER_LITERAL),
			Token.of(13, "]", TokenType.ARRAY_CLOSE),
			Token.of(14, "]", TokenType.ARRAY_CLOSE));
	}

	@Test
	void testMissingClosingArray() {
		assertThatThrownBy(() -> new Tokenizer("a[2+4", configuration).parse())
			.isEqualTo(new ParseException(1, 5, "a[2+4", "Closing array not found"));
	}

	@Test
	void testUnexpectedClosingArray() {
		assertThatThrownBy(() -> new Tokenizer("a[2+4]]", configuration).parse())
			.isEqualTo(new ParseException(7, 7, "]", "Unexpected closing array"));
	}

	@Test
	void testOpenArrayNotAllowedBeginning() {
		assertThatThrownBy(() -> new Tokenizer("[1]", configuration).parse())
			.isEqualTo(new ParseException(1, 1, "[", "Array open not allowed here"));
	}

	@Test
	void testOpenArrayNotAllowedAfterOperator() {
		assertThatThrownBy(() -> new Tokenizer("1+[1]", configuration).parse())
			.isEqualTo(new ParseException(3, 3, "[", "Array open not allowed here"));
	}

	@Test
	void testOpenArrayNotAllowedAfterBrace() {
		assertThatThrownBy(() -> new Tokenizer("([1]", configuration).parse())
			.isEqualTo(new ParseException(2, 2, "[", "Array open not allowed here"));
	}

	@Test
	void testCloseArrayNotAllowedBeginning() {
		assertThatThrownBy(() -> new Tokenizer("]", configuration).parse())
			.isEqualTo(new ParseException(1, 1, "]", "Array close not allowed here"));
	}

	@Test
	void testCloseArrayNotAllowedAfterBrace() {
		assertThatThrownBy(() -> new Tokenizer("(]", configuration).parse())
			.isEqualTo(new ParseException(2, 2, "]", "Array close not allowed here"));
	}

	@Test
	void testArraysNotAllowedOpen() {
		Configuration config = Configuration.builder().isArraysAllowed(false).build();

		assertThatThrownBy(() -> new Tokenizer("a[0]", config).parse())
			.isEqualTo(new ParseException(2, 2, "[", "Undefined operator '['"));
	}

	@Test
	void testArraysNotAllowedClose() {
		Configuration config = Configuration.builder().isArraysAllowed(false).build();

		assertThatThrownBy(() -> new Tokenizer("]", config).parse())
			.isEqualTo(new ParseException(1, 1, "]", "Undefined operator ']'"));
	}
}
