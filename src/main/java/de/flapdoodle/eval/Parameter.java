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

import de.flapdoodle.eval.functions.basic.Conditional;
import de.flapdoodle.eval.functions.basic.Min;
import de.flapdoodle.eval.functions.validations.ParameterValidator;
import de.flapdoodle.eval.parser.ASTNode;
import de.flapdoodle.eval.parser.Token;
import org.immutables.builder.Builder;
import org.immutables.value.Value;

import java.util.List;

/** Definition of a function parameter. */
@Value.Immutable
public interface Parameter<T extends de.flapdoodle.eval.data.Value<?>> {

  @Builder.Parameter
  Class<T> type();

  /** Name of the parameter, useful for error messages etc. */
  String name();

  /**
   * Whether this parameter is a variable argument parameter (can be repeated).
   *
   * @see Min for an example.
   */
  @Value.Default
  default boolean isVarArg() {
    return false;
  }

  @Value.Default
  default boolean isOptional() { return false; }

  /**
   * Set to true, the parameter will not be evaluated in advance, but the corresponding {@link
   * ASTNode} will be passed as a parameter value.
   *
   * @see Conditional for an example.
   */
  @Value.Default
  default boolean isLazy() {
    return false;
  }

  public abstract List<ParameterValidator<T>> validators();

  public static <T extends de.flapdoodle.eval.data.Value<?>> ImmutableParameter.Builder<T> builder(Class<T> type) {
    return ImmutableParameter.builder(type);
  }

  public static <T extends de.flapdoodle.eval.data.Value<?>> ImmutableParameter<T> of(Class<T> type, String name) {
    return builder(type).name(name).build();
  }

  public static <T extends de.flapdoodle.eval.data.Value<?>> ImmutableParameter<T> varArgWith(Class<T> type, String name) {
    return builder(type).name(name).isVarArg(true).build();
  }

  public static <T extends de.flapdoodle.eval.data.Value<?>> ImmutableParameter<T> optionalWith(Class<T> type, String name) {
    return builder(type).name(name).isOptional(true).build();
  }

  public static <T extends de.flapdoodle.eval.data.Value<?>> ImmutableParameter<T> lazyWith(Class<T> type, String name) {
    return builder(type).name(name).isLazy(true).build();
  }

  @Value.Auxiliary
  default void validate(Token token, de.flapdoodle.eval.data.Value<?> parameterValue) throws EvaluationException {
    if (type().isInstance(parameterValue)) {
      T value = type().cast(parameterValue);
      for (ParameterValidator<T> validator : validators()) {
        validator.validate(token, value);
      }
    } else {
      throw EvaluationException.ofUnsupportedDataTypeInOperation(token);
    }
  }
}