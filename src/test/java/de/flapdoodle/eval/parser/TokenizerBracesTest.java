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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenizerBracesTest extends BaseParserTest {

	@Test
	void testBracesSimple() throws ParseException {
		assertAllTokensParsedCorrectly(
			"(a + b)",
			CommonToken.of(0, "(", TokenType.BRACE_OPEN),
			CommonToken.of(1, "a", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(3, "+", TokenType.INFIX_OPERATOR),
			CommonToken.of(5, "b", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(6, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testBracesNested() throws ParseException {

		assertAllTokensParsedCorrectly(
			"(a+(b+c))",
			CommonToken.of(0, "(", TokenType.BRACE_OPEN),
			CommonToken.of(1, "a", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(2, "+", TokenType.INFIX_OPERATOR),
			CommonToken.of(3, "(", TokenType.BRACE_OPEN),
			CommonToken.of(4, "b", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(5, "+", TokenType.INFIX_OPERATOR),
			CommonToken.of(6, "c", TokenType.VARIABLE_OR_CONSTANT),
			CommonToken.of(7, ")", TokenType.BRACE_CLOSE),
			CommonToken.of(8, ")", TokenType.BRACE_CLOSE));
	}

	@Test
	void testMissingClosingBrace() {
		assertThatThrownBy(() -> new Tokenizer("(2+4", configuration).parse())
			.isEqualTo(new ParseException(1, 4, "(2+4", "Closing brace not found"));
	}

	@Test
	void testUnexpectedClosingBrace() {
		assertThatThrownBy(() -> new Tokenizer("(2+4))", configuration).parse())
			.isEqualTo(new ParseException(5, 5, ")", "Unexpected closing brace"));
	}
}
