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

import de.flapdoodle.eval.Nullable;
import de.flapdoodle.eval.functions.Function;
import de.flapdoodle.eval.operators.Operator;
import org.immutables.value.Value;

/**
 * A token represents a singe part of an expression, like an operator, number literal, or a brace.
 * Each token has a unique type, a value (its representation) and a position (starting with 1) in
 * the original expression string.
 *
 * <p>For operators and functions, the operator and function definition is also set during parsing.
 */
@Value.Immutable
public interface Token {

  @Value.Parameter
  int start();

  @Value.Parameter
  String value();

  @Value.Parameter
  TokenType type();

  @Nullable
  @Value.Auxiliary
  Function function();

  @Nullable
  @Value.Auxiliary
  Operator operator();

  @Value.Auxiliary
  default <T extends Operator> T operator(Class<T> operatorType) {
    Operator def = operator();
    if (operatorType.isInstance(def)) {
      return operatorType.cast(def);
    }
    throw new IllegalArgumentException("operator definition does not match: "+operatorType+" -> "+def);
  }

  static Token of(int startPosition, String value, TokenType type) {
    return ImmutableToken.of(startPosition, value, type);
  }

  static Token of(int startPosition, String value, TokenType type, Function function) {
    return ImmutableToken.of(startPosition, value, type).withFunction(function);
  }

  static Token of(int startPosition, String value, TokenType type, Operator operator) {
    return ImmutableToken.of(startPosition, value, type).withOperator(operator);
  }
}
