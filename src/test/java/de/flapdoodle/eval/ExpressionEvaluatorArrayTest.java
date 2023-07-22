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

import de.flapdoodle.eval.config.MapBasedVariableResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.config.VariableResolver;
import de.flapdoodle.eval.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionEvaluatorArrayTest extends BaseExpressionEvaluatorTest {

  @Test
  void testSimpleArray() throws ParseException, EvaluationException {
    Value.ArrayValue array = Value.of(Value::of, Arrays.asList(new BigDecimal(99)));
    MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().with("a", array);
    assertThat(evaluate("a[0]", mapBasedVariableResolver)).isEqualTo("99");
  }

  @Test
  void testMultipleEntriesArray() throws ParseException, EvaluationException {
    Value.ArrayValue array = Value.of(Value::of, new BigDecimal(2), new BigDecimal(4), new BigDecimal(6));
    MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().with("a", array);
    assertThat(evaluate("a[0]+a[1]+a[2]", mapBasedVariableResolver)).isEqualTo("12");
  }

  @Test
  void testExpressionArray() throws ParseException, EvaluationException {
    Value.ArrayValue array = Value.of(Value::of, new BigDecimal(3));
    MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().with("a", array).and("x", Value.of(new BigDecimal(4)));
    assertThat(evaluate("a[4-x]", mapBasedVariableResolver)).isEqualTo("3");
  }

  @Test
  void testNestedArray() throws ParseException, EvaluationException {
    MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty()
      .with("a", Value.of(Value::of, Arrays.asList(new BigDecimal(3))))
      .and("b", Value.of(Value::of, new BigDecimal(2), new BigDecimal(4), new BigDecimal(6)))
      .and("x", Value.of(new BigDecimal(6)));
    VariableResolver variableResolver = mapBasedVariableResolver;
    assertThat(evaluate("a[b[6-4]-x]", variableResolver)).isEqualTo("3");
  }

  @Test
  void testStringArray() throws ParseException, EvaluationException {
    Value.ArrayValue array = Value.of(Value::of, "Hello", "beautiful", "world");
    MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().with("a", array);
    assertThat(evaluate("a[0] + \" \" + a[1] + \" \" + a[2]", mapBasedVariableResolver)).isEqualTo("Hello beautiful world");
  }

  @Test
  void testBooleanArray() throws ParseException, EvaluationException {
    Value.ArrayValue array = Value.of(Value::of, true, true, false);
    MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().with("a", array);
    assertThat(evaluate("a[0] + \" \" + a[1] + \" \" + a[2]", mapBasedVariableResolver)).isEqualTo("true true false");
  }

  @Test
  void testArrayOfArray() throws EvaluationException, ParseException {
    Value.ArrayValue array = Value.of(
      Value.of(Value::of, new BigDecimal(1), new BigDecimal(2)),
      Value.of(Value::of, new BigDecimal(4), new BigDecimal(8))
    );

    MapBasedVariableResolver mapBasedVariableResolver3 = VariableResolver.empty().with("a", array);
    assertThat(evaluate("a[0][0]", mapBasedVariableResolver3)).isEqualTo("1");
    MapBasedVariableResolver mapBasedVariableResolver2 = VariableResolver.empty().with("a", array);
    assertThat(evaluate("a[0][1]", mapBasedVariableResolver2)).isEqualTo("2");
    MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty().with("a", array);
    assertThat(evaluate("a[1][0]", mapBasedVariableResolver1)).isEqualTo("4");
    MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().with("a", array);
    assertThat(evaluate("a[1][1]", mapBasedVariableResolver)).isEqualTo("8");
  }

  @Test
  void testMixedArray() throws ParseException, EvaluationException {
    Value.ArrayValue array = Value.of(Value.of("Hello"), Value.of(new BigDecimal(4)), Value.of(true));

    MapBasedVariableResolver mapBasedVariableResolver2 = VariableResolver.empty().with("a", array);
    assertThat(evaluate("a[0]", mapBasedVariableResolver2)).isEqualTo("Hello");
    MapBasedVariableResolver mapBasedVariableResolver1 = VariableResolver.empty().with("a", array);
    assertThat(evaluate("a[1]", mapBasedVariableResolver1)).isEqualTo("4");
    MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().with("a", array);
    assertThat(evaluate("a[2]", mapBasedVariableResolver)).isEqualTo("true");
  }

  @Test
  void testThrowsUnsupportedDataTypeForArray() {
    assertThatThrownBy(() -> {
      MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().with("a", Value.of("aString"));
      evaluate("a[0]", mapBasedVariableResolver);
    })
        .isInstanceOf(EvaluationException.class)
        .hasMessage("Unsupported data types in operation");
  }

  @Test
  void testThrowsUnsupportedDataTypeForIndex() {
    assertThatThrownBy(
            () -> {
              Value.ArrayValue array = Value.of(Value::of, "Hello");
              MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty().with("a", array).and("b", Value.of("anotherString"));
              evaluate("a[b]", mapBasedVariableResolver);
            })
        .isInstanceOf(EvaluationException.class)
        .hasMessage("Unsupported data types in operation");
  }
}
