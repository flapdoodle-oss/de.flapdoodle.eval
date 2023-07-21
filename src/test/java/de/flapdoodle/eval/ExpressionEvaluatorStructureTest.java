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

import de.flapdoodle.eval.data.*;
import de.flapdoodle.eval.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionEvaluatorStructureTest extends BaseExpressionEvaluatorTest {

  @Test
  void testStructureScientificNumberDistinction() throws EvaluationException, ParseException {
    Map<String, BigDecimal> structure =
        new HashMap<String, BigDecimal>() {
          {
            put("environment_id", new BigDecimal(12345));
          }
        };

    VariableResolver variableResolver = VariableResolver.builder()
      .with("order", Value::of, structure)
      .build();
    assertThat(evaluate("order.environment_id", variableResolver)).isEqualTo("12345");
  }

  @Test
  void testStructureScientificNumberDistinctionMultiple()
      throws EvaluationException, ParseException {
    ImmutableValueMap structure1 = ValueMap.builder()
      .putValues("e_id_e", Value.of(ValueMap.builder()
        .putValues("var_x", Value.of(ValueMap.builder()
          .putValues("e", Value.of(new BigDecimal("765")))
          .build()))
        .build()))
      .build();

    VariableResolver variableResolver = VariableResolver.builder()
      .with("order", structure1)
      .build();
    assertThat(evaluate("order.e_id_e.var_x.e", variableResolver)).isEqualTo("765");
  }

  @Test
  void testSimpleStructure() throws ParseException, EvaluationException {
    Map<String, BigDecimal> structure =
        new HashMap<String, BigDecimal>() {
          {
            put("b", new BigDecimal(99));
          }
        };

    VariableResolver variableResolver = VariableResolver.builder()
      .with("a", Value::of, structure)
      .build();
    assertThat(evaluate("a.b",variableResolver)).isEqualTo("99");
  }

  @Test
  void testTripleStructure() throws ParseException, EvaluationException {
    ImmutableValueMap structure = ValueMap.builder()
      .putValues("b", Value.of(ValueMap.builder()
        .putValues("c", Value.of(new BigDecimal(95)))
        .build()))
      .build();

    VariableResolver variableResolver = VariableResolver.builder()
      .with("a", structure)
      .build();
    assertThat(evaluate("a.b.c", variableResolver)).isEqualTo("95");
  }

  @Test
  void testThrowsUnsupportedDataTypeForStructure() {
    assertThatThrownBy(() -> {
      VariableResolver variableResolver = VariableResolver.builder().with("a", "aString").build();
      Expression expression = createExpression("a.b");
      expression.evaluate(variableResolver);
    })
        .isInstanceOf(EvaluationException.class)
        .hasMessage("Unsupported data types in operation");
  }

  @Test
  void testThrowsFieldNotFound() {
    Map<String, BigDecimal> testStructure = new HashMap<>();
    testStructure.put("field1", new BigDecimal(3));

    assertThatThrownBy(
            () -> {
              VariableResolver variableResolver = VariableResolver.builder()
                .with("a", Value::of, testStructure)
                .build();
              Expression expression = createExpression("a.field1 + a.field2");
              expression.evaluate(variableResolver);
            })
        .isInstanceOf(EvaluationException.class)
        .hasMessage("Field 'field2' not found in structure")
        .extracting("startPosition")
        .isEqualTo(14);
  }

  @Test
  void testStructureWithSpaceInName() throws EvaluationException, ParseException {
    Map<String, BigDecimal> testStructure = new HashMap<>();
    testStructure.put("field 1", new BigDecimal(88));


    VariableResolver variableResolver = VariableResolver.builder()
      .with("a", Value::of, testStructure)
      .build();
    assertThat(evaluate("a.\"field 1\"", variableResolver)).isEqualTo("88");
  }

  @Test
  void testTripleStructureWithSpaces() throws ParseException, EvaluationException {
    ImmutableValueMap structure = ValueMap.builder()
      .putValues("prop b", Value.of(ValueArray.of(
        Value.of(ValueMap.builder()
          .putValues("prop c", Value.of(99))
          .build())
      )))
      .build();

    VariableResolver variableResolver = VariableResolver.builder()
      .with("a", structure)
      .build();
    assertThat(evaluate("a.\"prop b\"[0].\"prop c\"", variableResolver)).isEqualTo("99.0");
  }

  @Test
  void testStructureWithSpaceInNameAndArrayAccess() throws EvaluationException, ParseException {
    ImmutableValueMap structure = ValueMap.builder()
      .putValues("b prop", Value.of(ValueArray.of(
        Value.of(1),Value.of(2),Value.of(3)
      )))
      .build();

    VariableResolver variableResolver = VariableResolver.builder()
      .with("a", structure)
      .build();
    assertThat(evaluate("a.\"b prop\"[1]", variableResolver)).isEqualTo("2.0");
  }
}
