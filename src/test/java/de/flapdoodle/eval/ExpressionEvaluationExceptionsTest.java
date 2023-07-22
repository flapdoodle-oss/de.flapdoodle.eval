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
package de.flapdoodle.eval;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.parser.ASTNode;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.parser.TokenType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionEvaluationExceptionsTest {

  @Test
  void testUnexpectedToken() {
    Expression expression = Expression.of("1");

    assertThatThrownBy(
            () -> {
              ASTNode node = ASTNode.of(Token.of(1, "(", TokenType.BRACE_OPEN));
              expression.evaluateSubtree(ValueResolver.empty(), node);
            })
        .isInstanceOf(EvaluationException.class)
        .hasMessage(
            "Unexpected evaluation token: Token{startPosition=1, value=(, type=BRACE_OPEN}");
  }
}
