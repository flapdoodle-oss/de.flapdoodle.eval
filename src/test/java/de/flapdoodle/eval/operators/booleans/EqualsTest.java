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
package de.flapdoodle.eval.operators.booleans;

import de.flapdoodle.eval.BaseEvaluationTest;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.config.MapBasedVariableResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.config.VariableResolver;
import de.flapdoodle.eval.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EqualsTest extends BaseEvaluationTest {

  @ParameterizedTest
  @CsvSource(
      delimiter = ':',
      value = {
        "1=1 : true",
        "1==1 : true",
        "0=0 : true",
        "1=0 : false",
        "0=1 : false",
        "21.678=21.678 : true",
        "\"abc\"=\"abc\" : true",
        "\"abc\"=\"xyz\" : false",
        "1+2=4-1 : true",
        "-5.2=-5.2 : true",
        "DT_DATE_TIME(2022,10,30)=DT_DATE_TIME(2022,10,30) : true",
        "DT_DATE_TIME(2022,10,30)=DT_DATE_TIME(2022,10,01) : false",
        "DT_DURATION_PARSE(\"PT24H\")=DT_DURATION_PARSE(\"P1D\") : true",
      })
  void testInfixEqualsLiterals(String expression, String expectedResult)
      throws EvaluationException, ParseException {
    assertExpressionHasExpectedResult(expression, expectedResult);
  }

  @Test
  void testInfixEqualsVariables() throws EvaluationException, ParseException {
    Expression expression = Expression.of("a=b");

    new BigDecimal("1.4");
    new BigDecimal("1.4");
    MapBasedVariableResolver mapBasedVariableResolver4 = VariableResolver.empty()
        .with("a", new BigDecimal("1.4"))
        .with("b", new BigDecimal("1.4"));
    assertThat(
            expression.evaluate(mapBasedVariableResolver4)
                .wrapped())
        .isEqualTo(Boolean.TRUE);

    MapBasedVariableResolver mapBasedVariableResolver3 = VariableResolver.empty().with("a", "Hello").with("b", "Hello");
    assertThat(expression.evaluate(mapBasedVariableResolver3).wrapped())
        .isEqualTo(Boolean.TRUE);

    MapBasedVariableResolver mapBasedVariableResolver2 = VariableResolver.empty().with("a", "Hello").with("b", "Goodbye");
    assertThat(expression.evaluate(mapBasedVariableResolver2).wrapped())
      .isEqualTo(Boolean.FALSE);

    MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty().with("a", true).with("b", true);
    assertThat(expression.evaluate(mapBasedVariableResolver1).wrapped())
      .isEqualTo(Boolean.TRUE);

    MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().with("a", false).with("b", true);
    assertThat(expression.evaluate(mapBasedVariableResolver).wrapped())
      .isEqualTo(Boolean.FALSE);
  }

  @Test
  void testInfixEqualsArrays() throws EvaluationException, ParseException {
    Expression expression = Expression.of("a=b");

    MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty()
        .with("a", Value::of, Arrays.asList("a", "b", "c"))
        .with("b", Value::of, Arrays.asList("a", "b", "c"));
    assertThat(
            expression.evaluate(mapBasedVariableResolver1)
                .wrapped())
        .isEqualTo(Boolean.TRUE);

    MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty()
        .with("a", Value::of, Arrays.asList("a", "b", "c"))
        .with("b", Value::of, Arrays.asList("c", "b", "a"));
    assertThat(
            expression.evaluate(mapBasedVariableResolver)
                .wrapped())
        .isEqualTo(false);
  }

  @Test
  void testInfixEqualsStructures() throws EvaluationException, ParseException {
    Expression expression = Expression.of("a=b");

    Map<String, BigDecimal> structure1 =
        new HashMap<String, BigDecimal>() {
          {
            put("a", new BigDecimal(35));
            put("b", new BigDecimal(99));
          }
        };

    Map<String, BigDecimal> structure2 =
        new HashMap<String, BigDecimal>() {
          {
            put("a", new BigDecimal(35));
            put("b", new BigDecimal(99));
          }
        };

    Map<String, BigDecimal> structure3 =
        new HashMap<String, BigDecimal>() {
          {
            put("a", new BigDecimal(45));
            put("b", new BigDecimal(99));
          }
        };

    MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty().with("a", Value::of, structure1).with("b", Value::of, structure2);
    assertThat(expression.evaluate(mapBasedVariableResolver1).wrapped())
        .isEqualTo(true);

    MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().with("a", Value::of, structure1).with("b", Value::of, structure3);
    assertThat(expression.evaluate(mapBasedVariableResolver).wrapped())
        .isEqualTo(false);
  }
}
