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

import de.flapdoodle.eval.config.MapBasedValueResolver;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.InfixOperator;
import de.flapdoodle.eval.operators.PostfixOperator;
import de.flapdoodle.eval.operators.PrefixOperator;
import de.flapdoodle.eval.parser.ASTNode;
import de.flapdoodle.eval.parser.ParseException;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionTest {

	@Test
	void testExpressionDefaults() {
		Expression expression = Expression.of("a+b");

		assertThat(expression.raw()).isEqualTo("a+b");
//    assertThat(expression.getConfiguration().getMathContext())
//        .isEqualTo(Configuration.DEFAULT_MATH_CONTEXT);
		assertThat(expression.configuration().functions().has("SUM")).isTrue();
		assertThat(expression.configuration().getOperatorResolver().hasOperator(InfixOperator.class, "+"))
			.isTrue();
		assertThat(expression.configuration().getOperatorResolver().hasOperator(PrefixOperator.class, "+"))
			.isTrue();
		assertThat(expression.configuration().getOperatorResolver().hasOperator(PostfixOperator.class, "+"))
			.isFalse();
	}

	@Test
	void testValidateOK() throws ParseException {
		Expression.of("1+1").validate();
	}

	@Test
	void testValidateFail() {
		assertThatThrownBy(() -> Expression.of("2#3").validate())
			.isInstanceOf(ParseException.class)
			.hasMessage("Undefined operator '#'");
	}

	@Test
	void testExpressionNode() throws ParseException, EvaluationException {
		Expression expression = Expression.of("a*b");
		ASTNode subExpression = expression.createExpressionNode("4+3");

		Expression expression1 = expression;
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("a", Value.of(2)).and("b", Value.of(subExpression));
		ValueResolver variableResolver = mapBasedVariableResolver;
		Value<?> result = expression1.evaluate(variableResolver);

		assertThat(result).isEqualTo(Value.of(14));
	}

	@Test
	void testWithValues() throws ParseException, EvaluationException {
		Expression expression = Expression.of("(a + b) * (a - b)");

		Map<String, Value<?>> values = new HashMap<>();
		values.put("a", Value.of(3.5));
		values.put("b", Value.of(2.5));

		Expression expression1 = expression;
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.withValues(values);
		ValueResolver variableResolver = mapBasedVariableResolver;
		Value.NumberValue result = (Value.NumberValue) expression1.evaluate(variableResolver);

		assertThat(result.wrapped()).isCloseTo(Value.of(6).wrapped(), Percentage.withPercentage(0.9999));
	}

	@Test
	void testWithValuesDoubleMap() throws ParseException, EvaluationException {
		Expression expression = Expression.of("a+b");

		Map<String, Value.NumberValue> values = new HashMap<>();
		values.put("a", Value.of(3.9));
		values.put("b", Value.of(3.1));

		Expression expression1 = expression;
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.withValues(values);
		ValueResolver variableResolver = mapBasedVariableResolver;
		Value<?> result = expression1.evaluate(variableResolver);

		assertThat(result).isEqualTo(Value.of(7));
	}

	@Test
	void testWithValuesStringMap() throws ParseException, EvaluationException {
		Expression expression = Expression.of("a+b+c");

		Map<String, Value.StringValue> values = new HashMap<>();
		values.put("a", Value.of("Hello"));
		values.put("b", Value.of(" "));
		values.put("c", Value.of("world"));

		Expression expression1 = expression;
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.withValues(values);
		ValueResolver variableResolver = mapBasedVariableResolver;
		Value<?> result = expression1.evaluate(variableResolver);

		assertThat(result.wrapped()).isEqualTo("Hello world");
	}

	@Test
	void testWithValuesMixedMap() throws ParseException, EvaluationException {
		Expression expression = Expression.of("a+b+c");

		Map<String, Value<?>> values = new HashMap<>();
		values.put("a", Value.of(true));
		values.put("b", Value.of(" "));
		values.put("c", Value.of(24.7));

		Expression expression1 = expression;
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.withValues(values);
		ValueResolver variableResolver = mapBasedVariableResolver;
		Value<?> result = expression1.evaluate(variableResolver);

		assertThat(result.wrapped()).isEqualTo("true 24.7");
	}

	@Test
	void testDoubleConverterDefaultMathContext() {
//    Expression defaultMathContextExpression = Expression.of("1");
		assertThat(Value.of(1.67987654321).wrapped())
			.isEqualByComparingTo("1.67987654321");
	}

//  @Test
//  void testDoubleConverterLimitedMathContext() {
//    Expression limitedMathContextExpression =
//        Expression.of(
//            "1", Configuration.builder().mathContext(new MathContext(3)).build());
//    assertThat(limitedMathContextExpression.convertDoubleValue(1.6789).getNumberValue())
//        .isEqualByComparingTo("1.68");
//  }

	@Test
	void testGetAllASTNodes() throws ParseException {
		Expression expression = Expression.of("1+2/3");
		List<ASTNode> nodes = expression.getAllASTNodes();
		assertThat(nodes.get(0).getToken().value()).isEqualTo("+");
		assertThat(nodes.get(1).getToken().value()).isEqualTo("1");
		assertThat(nodes.get(2).getToken().value()).isEqualTo("/");
		assertThat(nodes.get(3).getToken().value()).isEqualTo("2");
		assertThat(nodes.get(4).getToken().value()).isEqualTo("3");
	}

	@Test
	void testGetUsedVariables() throws ParseException {
		Expression expression = Expression.of("a/2*PI+MIN(e,b)");
		assertThat(expression.getUsedVariables()).containsExactlyInAnyOrder("a", "b");
	}

	@Test
	void testGetUsedVariablesLongNames() throws ParseException {
		Expression expression = Expression.of("var1/2*PI+MIN(var2,var3)");
		assertThat(expression.getUsedVariables()).containsExactlyInAnyOrder("var1", "var2", "var3");
	}

	@Test
	void testGetUsedVariablesNoVariables() throws ParseException {
		Expression expression = Expression.of("1/2");
		assertThat(expression.getUsedVariables()).isEmpty();
	}

	@Test
	void testGetUsedVariablesCaseSensitivity() throws ParseException {
		Expression expression = Expression.of("a+B*b-A/PI*(1/2)*pi+e-E+a");
		assertThat(expression.getUsedVariables()).containsExactlyInAnyOrder("a", "b");
	}

	@Test
	void testGetUndefinedVariables() throws ParseException {
		Expression expression1 = Expression.of("a+A+b+B+c+C+E+e+PI+x");
		Expression expression = expression1;
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("x", Value.of(1));
		ValueResolver variableResolver = mapBasedVariableResolver;
		assertThat(expression.getUndefinedVariables(variableResolver)).containsExactlyInAnyOrder("a", "b", "c");
	}
}
