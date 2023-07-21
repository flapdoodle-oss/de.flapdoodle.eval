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

import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.data.VariableResolver;
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
    assertThat(evaluate("a[0]", VariableResolver.builder().with("a", array).build())).isEqualTo("99");
  }

  @Test
  void testMultipleEntriesArray() throws ParseException, EvaluationException {
    Value.ArrayValue array = Value.of(Value::of, new BigDecimal(2), new BigDecimal(4), new BigDecimal(6));
    assertThat(evaluate("a[0]+a[1]+a[2]", VariableResolver. builder().with("a", array).build())).isEqualTo("12");
  }

  @Test
  void testExpressionArray() throws ParseException, EvaluationException {
    Value.ArrayValue array = Value.of(Value::of, new BigDecimal(3));
    assertThat(evaluate("a[4-x]", VariableResolver. builder().with("a", array).and("x", Value.of(new BigDecimal(4))).build())).isEqualTo("3");
  }

  @Test
  void testNestedArray() throws ParseException, EvaluationException {
    VariableResolver variableResolver = VariableResolver. builder()
      .with("a", Value.of(Value::of, Arrays.asList(new BigDecimal(3))))
      .and("b", Value.of(Value::of, new BigDecimal(2), new BigDecimal(4), new BigDecimal(6)))
      .and("x", Value.of(new BigDecimal(6)))
      .build();
    assertThat(evaluate("a[b[6-4]-x]", variableResolver)).isEqualTo("3");
  }

  @Test
  void testStringArray() throws ParseException, EvaluationException {
    Value.ArrayValue array = Value.of(Value::of, "Hello", "beautiful", "world");
    assertThat(evaluate("a[0] + \" \" + a[1] + \" \" + a[2]", VariableResolver. builder().with("a", array).build())).isEqualTo("Hello beautiful world");
  }

  @Test
  void testBooleanArray() throws ParseException, EvaluationException {
    Value.ArrayValue array = Value.of(Value::of, true, true, false);
    assertThat(evaluate("a[0] + \" \" + a[1] + \" \" + a[2]", VariableResolver. builder().with("a", array).build())).isEqualTo("true true false");
  }

  @Test
  void testArrayOfArray() throws EvaluationException, ParseException {
    Value.ArrayValue array = Value.of(
      Value.of(Value::of, new BigDecimal(1), new BigDecimal(2)),
      Value.of(Value::of, new BigDecimal(4), new BigDecimal(8))
    );

    assertThat(evaluate("a[0][0]", VariableResolver. builder().with("a", array).build())).isEqualTo("1");
    assertThat(evaluate("a[0][1]", VariableResolver. builder().with("a", array).build())).isEqualTo("2");
    assertThat(evaluate("a[1][0]", VariableResolver. builder().with("a", array).build())).isEqualTo("4");
    assertThat(evaluate("a[1][1]", VariableResolver. builder().with("a", array).build())).isEqualTo("8");
  }

  @Test
  void testMixedArray() throws ParseException, EvaluationException {
    Value.ArrayValue array = Value.of(Value.of("Hello"), Value.of(new BigDecimal(4)), Value.of(true));

    assertThat(evaluate("a[0]", VariableResolver. builder().with("a", array).build())).isEqualTo("Hello");
    assertThat(evaluate("a[1]", VariableResolver. builder().with("a", array).build())).isEqualTo("4");
    assertThat(evaluate("a[2]", VariableResolver. builder().with("a", array).build())).isEqualTo("true");
  }

  @Test
  void testThrowsUnsupportedDataTypeForArray() {
    assertThatThrownBy(() -> {
      evaluate("a[0]", VariableResolver. builder().with("a", Value.of("aString")).build());
    })
        .isInstanceOf(EvaluationException.class)
        .hasMessage("Unsupported data types in operation");
  }

  @Test
  void testThrowsUnsupportedDataTypeForIndex() {
    assertThatThrownBy(
            () -> {
              Value.ArrayValue array = Value.of(Value::of, "Hello");
              evaluate("a[b]", VariableResolver. builder().with("a", array).and("b", Value.of("anotherString")).build());
            })
        .isInstanceOf(EvaluationException.class)
        .hasMessage("Unsupported data types in operation");
  }
}
