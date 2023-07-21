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

import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.AbstractInfixOperator;
import de.flapdoodle.eval.operators.AbstractPostfixOperator;
import de.flapdoodle.eval.operators.AbstractPrefixOperator;
import de.flapdoodle.eval.operators.Precedence;
import de.flapdoodle.types.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenizerLiteralOperatorsTest extends BaseParserTest {

  @BeforeEach
  public void setup() {
    configuration =
        configuration.withAdditionalOperators(
            Pair.of("AND", new AndOperator()),
            Pair.of("OR", new OrOperator()),
            Pair.of("NOT", new NotOperator()),
            Pair.of("DENIED", new DeniedOperator()));
  }

  @Test
  void testAndOrNot() throws ParseException {
    assertAllTokensParsedCorrectly(
        "NOT a AND b DENIED OR NOT(c)",
      Token.of(1, "NOT", TokenType.PREFIX_OPERATOR),
      Token.of(5, "a", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(7, "AND", TokenType.INFIX_OPERATOR),
      Token.of(11, "b", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(13, "DENIED", TokenType.POSTFIX_OPERATOR),
      Token.of(20, "OR", TokenType.INFIX_OPERATOR),
      Token.of(23, "NOT", TokenType.PREFIX_OPERATOR),
      Token.of(26, "(", TokenType.BRACE_OPEN),
      Token.of(27, "c", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(28, ")", TokenType.BRACE_CLOSE));
  }

  static class AndOperator extends AbstractInfixOperator.Typed<Value.BooleanValue, Value.BooleanValue> {
    protected AndOperator() {
      super(Precedence.OPERATOR_PRECEDENCE_AND, Value.BooleanValue.class, Value.BooleanValue.class);
    }

    @Override protected Value<?> evaluateTyped(Expression expression, Token operatorToken, Value.BooleanValue leftOperand, Value.BooleanValue rightOperand)
      throws EvaluationException {
      return Value.of(leftOperand.wrapped() && rightOperand.wrapped());
    }
  }

  static class OrOperator extends AbstractInfixOperator.Typed<Value.BooleanValue, Value.BooleanValue> {
    protected OrOperator() {
      super(Precedence.OPERATOR_PRECEDENCE_OR, Value.BooleanValue.class, Value.BooleanValue.class);
    }

    @Override protected Value<?> evaluateTyped(Expression expression, Token operatorToken, Value.BooleanValue leftOperand, Value.BooleanValue rightOperand)
      throws EvaluationException {
      return Value.of(leftOperand.wrapped() || rightOperand.wrapped());
    }
  }

  static class NotOperator extends AbstractPrefixOperator.Typed<Value.BooleanValue> {
    protected NotOperator() {
      super(Precedence.OPERATOR_PRECEDENCE_UNARY, false, Value.BooleanValue.class);
    }

    @Override protected Value<?> evaluateTyped(Expression expression, Token operatorToken, Value.BooleanValue operand) throws EvaluationException {
      return Value.of(!operand.wrapped());
    }
  }

  static class DeniedOperator extends AbstractPostfixOperator.Typed<Value.BooleanValue> {
    protected DeniedOperator() {
        super(Precedence.OPERATOR_PRECEDENCE_UNARY, Value.BooleanValue.class);
      }

    @Override protected Value<?> evaluateTyped(Expression expression, Token operatorToken, Value.BooleanValue operand) throws EvaluationException {
      return Value.of(!operand.wrapped());
    }
  }
}
