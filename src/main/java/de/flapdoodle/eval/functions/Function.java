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

import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.config.VariableResolver;
import de.flapdoodle.eval.parser.Token;

import java.util.List;

/**
 * Interface that is required for all functions in a function dictionary for evaluation of
 * expressions.
 */
public interface Function {
  
  /**
   * Returns the list of parameter definitions. Is never empty or <code>null</code>.
   *
   * @return The parameter definition list.
   */
  List<FunctionParameterDefinition<?>> parameterDefinitions();

  default int minArgs() {
    if (parameterDefinitions().isEmpty()) return 0;
    FunctionParameterDefinition<?> last = parameterDefinitions().get(parameterDefinitions().size() - 1);
    return parameterDefinitions().size() - (last.isOptional() ? 1 : 0);
  }

  default int maxArgs() {
    if (parameterDefinitions().isEmpty()) return 0;
    FunctionParameterDefinition<?> last = parameterDefinitions().get(parameterDefinitions().size() - 1);
    return last.isVarArg()
      ? Integer.MAX_VALUE
      : parameterDefinitions().size();
  }

  /**
   * Performs the function logic and returns an evaluation result.
   *
   * @param variableResolver
   * @param expression       The expression, where this function is executed. Can be used to access the
   *                         expression configuration.
   * @param functionToken    The function token from the parsed expression.
   * @param parameterValues  The parameter values.
   * @return The evaluation result in form of a {@link Value}.
   * @throws EvaluationException In case there were problems during evaluation.
   */
  Value<?> evaluate(
    VariableResolver variableResolver, Expression expression, Token functionToken, List<Value<?>> parameterValues)
      throws EvaluationException;

  default Value<?> evaluateUnvalidated(
    VariableResolver variableResolver, Expression expression, Token functionToken, List<Value<?>> parameterValues)
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

    for (int i = 0; i < parameterValues.size(); i++) {
      FunctionParameterDefinition<?> definition = parameterDefinition(i);
      definition.validatePreEvaluation(token, parameterValues.get(i));
//      for (ParameterValidator validator : definition.validators()) {
//        validator.validate(token, parameterValues[i]);
//      }
    }
    // TODO check min, max number of parameters..
    // see parser.de.flapdoodle.eval.ShuntingYardConverter.validateFunctionParameters
  }
  /**
   * Checks whether the function has a variable number of arguments parameter.
   *
   * @return <code>true</code> or <code>false</code>:
   */
  default boolean hasVarArgs() {
    return !parameterDefinitions().isEmpty() && parameterDefinitions().get(parameterDefinitions().size()-1).isVarArg();
  }

  default boolean hasOptional() {
    return !parameterDefinitions().isEmpty() && parameterDefinitions().get(parameterDefinitions().size()-1).isOptional();
  }

  /**
   * Checks if the parameter is a lazy parameter.
   *
   * @param parameterIndex The parameter index, starts at 0 for the first parameter. If the index is
   *     bigger than the list of parameter definitions, the last parameter definition will be
   *     checked.
   * @return <code>true</code> if the specified parameter is defined as lazy.
   */
  default boolean parameterIsLazy(int parameterIndex) {
    return parameterDefinition(parameterIndex).isLazy();
  }

  default FunctionParameterDefinition<?> parameterDefinition(int index) {
    if (hasVarArgs() && index >= parameterDefinitions().size()) {
      index = parameterDefinitions().size() - 1;
    }
    return parameterDefinitions().get(index);
  }

}
