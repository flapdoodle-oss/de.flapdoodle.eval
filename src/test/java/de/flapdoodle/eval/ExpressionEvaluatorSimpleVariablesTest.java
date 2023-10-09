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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionEvaluatorSimpleVariablesTest extends BaseExpressionEvaluatorTest {

	@Test
	void testSingleStringVariable() throws ParseException, EvaluationException {
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("a", "hello");
		ValueResolver variableResolver = mapBasedVariableResolver;
		Value<?> result = createExpression("a").evaluate(variableResolver);
		assertThat(result.wrapped()).isEqualTo("hello");
	}

	@Test
	void testSingleNumberVariable() throws ParseException, EvaluationException {
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("a", BigDecimal.valueOf(9));
		ValueResolver variableResolver = mapBasedVariableResolver;
		Value<?> result = createExpression("a").evaluate(variableResolver);
		assertThat(result.wrapped()).isEqualTo(BigDecimal.valueOf(9));
	}

	@Test
	void testNumbers() throws ParseException, EvaluationException {
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("a", BigDecimal.valueOf(9))
			.with("b", BigDecimal.valueOf(5));
		ValueResolver variableResolver = mapBasedVariableResolver;
		Value<?> result = createExpression("(a+b)*(a-b)").evaluate(variableResolver);
		assertThat(result.wrapped()).isEqualTo(BigDecimal.valueOf(56));
	}

	@Test
	void testStrings() throws ParseException, EvaluationException {
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("prefix", "Hello")
			.with("infix", " ")
			.with("postfix", "world");
		ValueResolver variableResolver = mapBasedVariableResolver;
		Value<?> result = createExpression("prefix+infix+postfix").evaluate(variableResolver);
		assertThat(result.wrapped()).isEqualTo("Hello world");
	}

	@Test
	void testStringNumberCombined() throws ParseException, EvaluationException {
		MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
			.with("prefix", "Hello")
			.with("infix", BigDecimal.valueOf(2))
			.with("postfix", "world");
		ValueResolver variableResolver = mapBasedVariableResolver;
		Value<?> result = createExpression("prefix+infix+postfix").evaluate(variableResolver);
		assertThat(result.wrapped()).isEqualTo("Hello2world");
	}

	@Test
	void testUnknownVariable() {
		assertThatThrownBy(() -> createExpression("a").evaluate(ValueResolver.empty()))
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Variable or constant value for 'a' not found");
	}
}
