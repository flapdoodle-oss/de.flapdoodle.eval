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

import de.flapdoodle.eval.core.Expression;
import de.flapdoodle.eval.core.MapBasedVariableResolver;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.ParseException;
import de.flapdoodle.eval.values.ImmutableValueMap;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueMap;
import de.flapdoodle.eval.values.Values;
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

		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("order", Value.of(Value::of, structure));
		VariableResolver variableResolver = mapBasedVariableResolver;
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

		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("order", Value.of(structure1));
		VariableResolver variableResolver = mapBasedVariableResolver;
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

		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Value.of(Value::of, structure));
		VariableResolver variableResolver = mapBasedVariableResolver;
		assertThat(evaluate("a.b", variableResolver)).isEqualTo("99");
	}

	@Test
	void testSimpleStructureWithIndexAccess() throws ParseException, EvaluationException {
		Map<String, BigDecimal> structure =
			new HashMap<String, BigDecimal>() {
				{
					put("b", new BigDecimal(99));
				}
			};

		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Value.of(Value::of, structure));
		VariableResolver variableResolver = mapBasedVariableResolver;
		assertThat(evaluate("a[\"b\"]", variableResolver)).isEqualTo("99");
	}

	@Test
	void testTripleStructure() throws ParseException, EvaluationException {
		ImmutableValueMap structure = ValueMap.builder()
			.putValues("b", Value.of(ValueMap.builder()
				.putValues("c", Value.of(new BigDecimal(95)))
				.build()))
			.build();

		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Value.of(structure));
		VariableResolver variableResolver = mapBasedVariableResolver;
		assertThat(evaluate("a.b.c", variableResolver)).isEqualTo("95");
	}

	@Test
	void testThrowsUnsupportedDataTypeForStructure() {
		assertThatThrownBy(() -> {
            MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
            MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Value.of("aString"));
			VariableResolver variableResolver = mapBasedVariableResolver;
			Expression expression = createExpression("a.b");
			expression.evaluate(variableResolver);
		})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("wrong type: class de.flapdoodle.eval.values.Value$MapValue != class de.flapdoodle.eval.values.ImmutableStringValue (aString)");
	}

	@Test
	void testThrowsFieldNotFound() {
		Map<String, BigDecimal> testStructure = new HashMap<>();
		testStructure.put("field1", new BigDecimal(3));

		assertThatThrownBy(
			() -> {
				MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
				MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Value.of(Value::of, testStructure));
				VariableResolver variableResolver = mapBasedVariableResolver;
				Expression expression = createExpression("a.field1 + a.field2");
				expression.evaluate(variableResolver);
			})
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Field 'field2' not found in structure")
			.extracting("startPosition")
			.isEqualTo(12);
	}

	@Test
	void testStructureWithSpaceInName() throws EvaluationException, ParseException {
		Map<String, BigDecimal> testStructure = new HashMap<>();
		testStructure.put("field 1", new BigDecimal(88));

		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Value.of(Value::of, testStructure));
		VariableResolver variableResolver = mapBasedVariableResolver;
		assertThat(evaluate("a.\"field 1\"", variableResolver)).isEqualTo("88");
	}

	@Test
	void testTripleStructureWithSpaces() throws ParseException, EvaluationException {
		ImmutableValueMap structure = ValueMap.builder()
			.putValues("prop b", Value.of(Values.of(
				Value.of(ValueMap.builder()
					.putValues("prop c", Value.of(99))
					.build())
			)))
			.build();

		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Value.of(structure));
		VariableResolver variableResolver = mapBasedVariableResolver;
		assertThat(evaluate("a.\"prop b\"[0].\"prop c\"", variableResolver)).isEqualTo("99.0");
	}

	@Test
	void testStructureWithSpaceInNameAndArrayAccess() throws EvaluationException, ParseException {
		ImmutableValueMap structure = ValueMap.builder()
			.putValues("b prop", Value.of(Values.of(
				Value.of(1), Value.of(2), Value.of(3)
			)))
			.build();

		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		MapBasedVariableResolver mapBasedVariableResolver = mapBasedValueResolver.with("a", Value.of(structure));
		VariableResolver variableResolver = mapBasedVariableResolver;
		assertThat(evaluate("a.\"b prop\"[1]", variableResolver)).isEqualTo("2.0");
	}
}
