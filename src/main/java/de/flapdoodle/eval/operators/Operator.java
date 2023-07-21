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
package de.flapdoodle.eval.operators;

/**
 * Interface that is required for all operators in an operator dictionary for evaluation of
 * expressions. There are three operator type: prefix, postfix and infix. Every operator has a
 * precedence, which defines the order of operator evaluation. The associativity of an operator is a
 * property that determines how operators of the same precedence are grouped in the absence of
 * parentheses.
 */
public interface Operator {
  /**
   * @return The operator's precedence.
   */
  int getPrecedence();

  /**
   * If operators with same precedence are evaluated from left to right.
   *
   * @return The associativity.
   */
  boolean isLeftAssociative();

  OperatorType type();

//  /**
//   * Performs the operator logic and returns an evaluation result.
//   *
//   * @param expression The expression, where this function is executed. Can be used to access the
//   *     expression configuration.
//   * @param operatorToken The operator token from the parsed expression.
//   * @param operands The operands, one for prefix and postfix operators, two for infix operators.
//   * @return The evaluation result in form of a {@link Value}.
//   * @throws EvaluationException In case there were problems during evaluation.
//   */
//  Value<?> evaluate(Expression expression, Token operatorToken, List<Value<?>> operands)
//      throws EvaluationException;
}
