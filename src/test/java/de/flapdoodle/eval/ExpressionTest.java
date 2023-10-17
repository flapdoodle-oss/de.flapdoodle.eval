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

import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.ParseException;
import de.flapdoodle.eval.tree.Node;
import de.flapdoodle.eval.values.Value;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionTest {

	@Test
	void testExpressionDefaults() throws ParseException, EvaluationException {
		ImmutableExpressionFactory expressionFactory = ExpressionFactory.defaults();
		Expression expression = expressionFactory.parse("a+b");

		assertThat(expressionFactory.evaluatables().find("sum",2)).isNotEmpty();
		assertThat(expressionFactory.operatorMap().infixOperator("+"))
			.isNotEmpty();
		assertThat(expressionFactory.operatorMap().prefixOperator( "+"))
			.isNotEmpty();
		assertThat(expressionFactory.operatorMap().postfixOperator( "+"))
			.isEmpty();
	}

	@Test
	void testValidateOK() throws ParseException, EvaluationException {
		ExpressionFactory.defaults().parse("1+1");
	}

	@Test
	void testValidateFail() {
		assertThatThrownBy(() -> ExpressionFactory.defaults().parse("2#3"))
			.isInstanceOf(ParseException.class)
			.hasMessage("Undefined operator '#'");
	}

	@Test
	void testWithValues() throws ParseException, EvaluationException {
		Expression expression = ExpressionFactory.defaults().parse("(a + b) * (a - b)");

		Map<String, Value<?>> values = new HashMap<>();
		values.put("a", Value.of(3.5));
		values.put("b", Value.of(2.5));

		MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty()
			.withValues(values);
		Value.NumberValue result = (Value.NumberValue) expression.evaluate(mapBasedVariableResolver);

		assertThat(result.wrapped()).isCloseTo(Value.of(6).wrapped(), Percentage.withPercentage(0.9999));
	}

	@Test
	void testWithValuesDoubleMap() throws ParseException, EvaluationException {
		Expression expression = ExpressionFactory.defaults().parse("a+b");

		Map<String, Value.NumberValue> values = new HashMap<>();
		values.put("a", Value.of(3.9));
		values.put("b", Value.of(3.1));

		MapBasedVariableResolver variableResolver = VariableResolver.empty()
			.withValues(values);
		Object result = expression.evaluate(variableResolver);

		assertThat(result).isEqualTo(Value.of(7));
	}

	@Test
	void testWithValuesStringMap() throws ParseException, EvaluationException {
		Expression expression = ExpressionFactory.defaults().parse("a+b+c");

		Map<String, Value.StringValue> values = new HashMap<>();
		values.put("a", Value.of("Hello"));
		values.put("b", Value.of(" "));
		values.put("c", Value.of("world"));

		MapBasedVariableResolver mapBasedVariableResolver = VariableResolver.empty()
			.withValues(values);
		Object result = expression.evaluate(mapBasedVariableResolver);

		assertThat(result).isEqualTo(Value.of("Hello world"));
	}

	@Test
	void testWithValuesMixedMap() throws ParseException, EvaluationException {
		Expression expression = ExpressionFactory.defaults().parse("a+b+c");

		Map<String, Value<?>> values = new HashMap<>();
		values.put("a", Value.of(true));
		values.put("b", Value.of(" "));
		values.put("c", Value.of(24.7));

		VariableResolver variableResolver = VariableResolver.empty()
			.withValues(values);
		Object result = expression.evaluate(variableResolver);

		assertThat(result.toString()).isEqualTo("true 24.7");
	}

	@Test
	void testGetAllASTNodes() throws ParseException, EvaluationException {
		Expression expression = ExpressionFactory.defaults().parse("1+2/3");
		List<Node> nodes = expression.allNodes();
		assertThat(nodes.get(0).token().value()).isEqualTo("+");
		assertThat(nodes.get(1).token().value()).isEqualTo("1");
		assertThat(nodes.get(2).token().value()).isEqualTo("/");
		assertThat(nodes.get(3).token().value()).isEqualTo("2");
		assertThat(nodes.get(4).token().value()).isEqualTo("3");
	}

	@Test
	void testGetUsedVariables() throws ParseException, EvaluationException {
		Expression expression = ExpressionFactory.defaults().parse("a/2*PI+min(E,b)");
		assertThat(expression.usedVariables()).containsExactlyInAnyOrder("a", "b");
	}

	@Test
	void testGetUsedVariablesLongNames() throws ParseException, EvaluationException {
		Expression expression = ExpressionFactory.defaults().parse("var1/2*PI+min(var2,var3)");
		assertThat(expression.usedVariables()).containsExactlyInAnyOrder("var1", "var2", "var3");
	}

	@Test
	void testGetUsedVariablesNoVariables() throws ParseException, EvaluationException {
		Expression expression = ExpressionFactory.defaults().parse("1/2");
		assertThat(expression.usedVariables()).isEmpty();
	}

	@Test
	void testGetUsedVariablesCaseSensitivity() throws ParseException, EvaluationException {
		Expression expression = ExpressionFactory.defaults().parse("a+B*b-A/PI*(1/2)*PI+e-E+a");
		assertThat(expression.usedVariables()).containsExactlyInAnyOrder("A", "a", "B", "b", "e");
	}

	@Test
	void testGetUndefinedVariables() throws ParseException, EvaluationException {
		Expression expression = ExpressionFactory.defaults().parse("a+A+b+B+c+C+E+e+PI+x");
		VariableResolver variableResolver = VariableResolver.empty()
			.with("x", Value.of(1));
		assertThat(expression.undefinedVariables(variableResolver)).containsExactlyInAnyOrder("A", "a", "B", "b", "C", "c", "e");
	}
}
