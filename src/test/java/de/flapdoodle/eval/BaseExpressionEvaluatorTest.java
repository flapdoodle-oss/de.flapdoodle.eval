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

import de.flapdoodle.eval.config.Configuration;
import de.flapdoodle.eval.config.TestConfigurationProvider;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.config.VariableResolver;
import de.flapdoodle.eval.parser.ParseException;

import java.math.BigDecimal;

public abstract class BaseExpressionEvaluatorTest {

  final Configuration configuration =
      TestConfigurationProvider.StandardConfigurationWithAdditionalTestOperators;

  protected String evaluate(String expressionString) throws ParseException, EvaluationException {
    Expression expression = createExpression(expressionString);
    return expression.evaluate(VariableResolver.empty()).wrapped().toString();
  }

  protected String evaluate(String expressionString, VariableResolver variableResolver) throws ParseException, EvaluationException {
    Expression expression = createExpression(expressionString);
    return expression.evaluate(variableResolver).wrapped().toString();
  }

  Expression createExpression(String expressionString) {
    return Expression.of(expressionString, configuration);
  }

  protected static Value.NumberValue numberValueOf(String doubleAsString) {
    return Value.of(new BigDecimal(doubleAsString));
  }
}
