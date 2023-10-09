/**
 * Copyright (C) 2023
 * Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.eval;

import de.flapdoodle.eval.parser.ParseException;
import de.flapdoodle.eval.values.MapBasedValueResolver;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionEvaluatorArrayTest extends BaseExpressionEvaluatorTest {

	@Test
	void testSimpleArray() throws ParseException, EvaluationException {
		Value.ArrayValue array = Value.of(Value::of, Arrays.asList(new BigDecimal(99)));
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().with("a", array);
		assertThat(evaluate("a[0]", mapBasedVariableResolver)).isEqualTo("99");
	}

	@Test
	void testMultipleEntriesArray() throws ParseException, EvaluationException {
		Value.ArrayValue array = Value.of(Value::of, new BigDecimal(2), new BigDecimal(4), new BigDecimal(6));
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().with("a", array);
		assertThat(evaluate("a[0]+a[1]+a[2]", mapBasedVariableResolver)).isEqualTo("12");
	}

	@Test
	void testExpressionArray() throws ParseException, EvaluationException {
		Value.ArrayValue array = Value.of(Value::of, new BigDecimal(3));
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().with("a", array).and("x", Value.of(new BigDecimal(4)));
		assertThat(evaluate("a[4-x]", mapBasedVariableResolver)).isEqualTo("3");
	}

	@Test
	void testNestedArray() throws ParseException, EvaluationException {
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("a", Value.of(Value::of, Arrays.asList(new BigDecimal(3))))
			.and("b", Value.of(Value::of, new BigDecimal(2), new BigDecimal(4), new BigDecimal(6)))
			.and("x", Value.of(new BigDecimal(6)));
		ValueResolver variableResolver = mapBasedVariableResolver;
		assertThat(evaluate("a[b[6-4]-x]", variableResolver)).isEqualTo("3");
	}

	@Test
	void testStringArray() throws ParseException, EvaluationException {
		Value.ArrayValue array = Value.of(Value::of, "Hello", "beautiful", "world");
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().with("a", array);
		assertThat(evaluate("a[0] + \" \" + a[1] + \" \" + a[2]", mapBasedVariableResolver)).isEqualTo("Hello beautiful world");
	}

	@Test
	void testBooleanArray() throws ParseException, EvaluationException {
		Value.ArrayValue array = Value.of(Value::of, true, true, false);
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().with("a", array);
		assertThat(evaluate("a[0] + \" \" + a[1] + \" \" + a[2]", mapBasedVariableResolver)).isEqualTo("true true false");
	}

	@Test
	void testArrayOfArray() throws EvaluationException, ParseException {
		Value.ArrayValue array = Value.of(
			Value.of(Value::of, new BigDecimal(1), new BigDecimal(2)),
			Value.of(Value::of, new BigDecimal(4), new BigDecimal(8))
		);

		MapBasedValueResolver mapBasedVariableResolver3 = ValueResolver.empty().with("a", array);
		assertThat(evaluate("a[0][0]", mapBasedVariableResolver3)).isEqualTo("1");
		MapBasedValueResolver mapBasedVariableResolver2 = ValueResolver.empty().with("a", array);
		assertThat(evaluate("a[0][1]", mapBasedVariableResolver2)).isEqualTo("2");
		MapBasedValueResolver mapBasedVariableResolver1 = ValueResolver.empty().with("a", array);
		assertThat(evaluate("a[1][0]", mapBasedVariableResolver1)).isEqualTo("4");
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().with("a", array);
		assertThat(evaluate("a[1][1]", mapBasedVariableResolver)).isEqualTo("8");
	}

	@Test
	void testMixedArray() throws ParseException, EvaluationException {
		Value.ArrayValue array = Value.of(Value.of("Hello"), Value.of(new BigDecimal(4)), Value.of(true));

		MapBasedValueResolver mapBasedVariableResolver2 = ValueResolver.empty().with("a", array);
		assertThat(evaluate("a[0]", mapBasedVariableResolver2)).isEqualTo("Hello");
		MapBasedValueResolver mapBasedVariableResolver1 = ValueResolver.empty().with("a", array);
		assertThat(evaluate("a[1]", mapBasedVariableResolver1)).isEqualTo("4");
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().with("a", array);
		assertThat(evaluate("a[2]", mapBasedVariableResolver)).isEqualTo("true");
	}

	@Test
	void testThrowsUnsupportedDataTypeForArray() {
		assertThatThrownBy(() -> {
			MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().with("a", Value.of("aString"));
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
				MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty().with("a", array).and("b", Value.of("anotherString"));
				evaluate("a[b]", mapBasedVariableResolver);
			})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Unsupported data types in operation");
	}
}
