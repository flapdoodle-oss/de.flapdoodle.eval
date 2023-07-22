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
package de.flapdoodle.eval.functions;

import de.flapdoodle.eval.Evaluateable;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.Parameter;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.util.List;

/**
 * Interface that is required for all functions in a function dictionary for evaluation of
 * expressions.
 */
public interface Function extends Evaluateable {

  default Value<?> evaluateUnvalidated(
    ValueResolver variableResolver, Expression expression, Token functionToken, List<Value<?>> parameterValues)
    throws EvaluationException {
    validatePreEvaluation(functionToken, parameterValues);
    return evaluate(variableResolver, expression, functionToken, parameterValues);
  }

  /**
   * Validates the evaluation parameters, called before the actual evaluation.
   *
   * @param token The function token.
   * @param parameterValues The parameter values
   * @throws EvaluationException in case of any validation error
   */
  default void validatePreEvaluation(Token token, List<Value<?>> parameterValues)
    throws EvaluationException {
    parameters().validate(token, parameterValues);
  }
}
