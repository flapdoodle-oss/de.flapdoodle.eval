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
import de.flapdoodle.eval.operators.InfixOperator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenTest {

	final Configuration expressionConfiguration =
		Configuration.defaultConfiguration();

	@Test
	void testTokenCreation() {
		int counter = 0;
		for (TokenType type : TokenType.values()) {
			counter++;
			String tokenString =
				type == TokenType.NUMBER_LITERAL ? Integer.toString(counter) : "token" + counter;
			Token token = Token.of(counter, tokenString, type);

			assertThat(token.type()).isEqualTo(type);
			assertThat(token.start()).isEqualTo(counter);
			assertThat(token.value()).isEqualTo(tokenString);
			assertThat(token.function()).isNull();
			assertThat(token.operator()).isNull();
		}
	}

	@Test
	void testFunctionToken() {
		Token token =
			Token.of(
				3,
				"MAX",
				TokenType.FUNCTION,
				expressionConfiguration.functions().get("MAX"));

		assertThat(token.start()).isEqualTo(3);
		assertThat(token.value()).isEqualTo("MAX");
		assertThat(token.type()).isEqualTo(TokenType.FUNCTION);
		assertThat(token.function()).isNotNull();
		assertThat(token.operator()).isNull();
	}

	@Test
	void testOperatorToken() {
		Token token =
			Token.of(
				1,
				"+",
				TokenType.INFIX_OPERATOR,
				expressionConfiguration.getOperatorResolver().get(InfixOperator.class, "+"));

		assertThat(token.start()).isEqualTo(1);
		assertThat(token.value()).isEqualTo("+");
		assertThat(token.type()).isEqualTo(TokenType.INFIX_OPERATOR);
		assertThat(token.function()).isNull();
		assertThat(token.operator()).isNotNull();
	}
}
