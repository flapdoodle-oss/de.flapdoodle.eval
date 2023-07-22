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
package de.flapdoodle.eval.functions.trigonometric;

import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.config.VariableResolver;
import de.flapdoodle.eval.parser.Token;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.valueOf;

public class AsinRadians extends AbstractNumberFunction {

  private static final BigDecimal MINUS_ONE = valueOf(-1);

  @Override public Value<?> evaluate(VariableResolver variableResolver, Expression expression, Token functionToken,
    Value.NumberValue parameterValueX) throws EvaluationException {
    BigDecimal parameterValue = parameterValueX.wrapped();

    // validation
    if (parameterValue.compareTo(ONE) > 0) {
      throw new EvaluationException(
          functionToken, "Illegal asinr(x) for x > 1: x = " + parameterValue);
    }
    if (parameterValue.compareTo(MINUS_ONE) < 0) {
      throw new EvaluationException(
          functionToken, "Illegal asinr(x) for x < -1: x = " + parameterValue);
    }
    return Value.of(
        Math.asin(parameterValueX.wrapped().doubleValue()));
  }
}
