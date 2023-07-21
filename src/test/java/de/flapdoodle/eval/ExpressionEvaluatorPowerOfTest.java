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
import de.flapdoodle.eval.data.VariableResolver;
import de.flapdoodle.eval.operators.arithmetic.PowerOf;
import de.flapdoodle.eval.parser.ParseException;
import de.flapdoodle.types.Pair;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExpressionEvaluatorPowerOfTest extends BaseExpressionEvaluatorTest {

  @Test
  void testPrecedenceDefault() throws ParseException, EvaluationException {
    assertThat(evaluate("-2^2")).isEqualTo("4.0");
  }

  @Test
  void testPrecedenceHigher() throws ParseException, EvaluationException {
    Configuration config =
        Configuration.defaultConfiguration()
          .withAdditionalOperators(Pair.of("^", new PowerOf() {
            @Override public int getPrecedence() {
              return 100;
            }
          }));

    Expression expression = Expression.of("-2^2", config);

    assertThat(expression.evaluate(VariableResolver.empty()).wrapped().toString()).isEqualTo("-4.0");
  }
}
