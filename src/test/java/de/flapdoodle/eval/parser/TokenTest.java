/**
 * Copyright (C) 2023
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
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

      assertThat(token.getType()).isEqualTo(type);
      assertThat(token.getStartPosition()).isEqualTo(counter);
      assertThat(token.getValue()).isEqualTo(tokenString);
      assertThat(token.getFunctionDefinition()).isNull();
      assertThat(token.getOperatorDefinition()).isNull();
    }
  }

  @Test
  void testFunctionToken() {
    Token token =
      Token.of(
          3,
          "MAX",
          TokenType.FUNCTION,
          expressionConfiguration.getFunctionResolver().getFunction("MAX"));

    assertThat(token.getStartPosition()).isEqualTo(3);
    assertThat(token.getValue()).isEqualTo("MAX");
    assertThat(token.getType()).isEqualTo(TokenType.FUNCTION);
    assertThat(token.getFunctionDefinition()).isNotNull();
    assertThat(token.getOperatorDefinition()).isNull();
  }

  @Test
  void testOperatorToken() {
		Token token =
      Token.of(
          1,
          "+",
          TokenType.INFIX_OPERATOR,
				expressionConfiguration.getOperatorResolver().getOperator(InfixOperator.class, "+"));

    assertThat(token.getStartPosition()).isEqualTo(1);
    assertThat(token.getValue()).isEqualTo("+");
    assertThat(token.getType()).isEqualTo(TokenType.INFIX_OPERATOR);
    assertThat(token.getFunctionDefinition()).isNull();
    assertThat(token.getOperatorDefinition()).isNotNull();
  }
}
