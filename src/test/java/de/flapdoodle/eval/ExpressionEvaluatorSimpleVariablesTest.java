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

import de.flapdoodle.eval.core.MapBasedVariableResolver;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.ParseException;
import de.flapdoodle.eval.values.Value;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionEvaluatorSimpleVariablesTest extends BaseExpressionEvaluatorTest {

	@Test
	void testSingleStringVariable() throws ParseException, EvaluationException {
        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
        VariableResolver variableResolver = mapBasedValueResolver.with("a", Value.of("hello"));
		Object result = createExpression("a").evaluate(variableResolver);
		assertThat(result).isEqualTo(Value.of("hello"));
	}

	@Test
	void testSingleNumberVariable() throws ParseException, EvaluationException {
		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		VariableResolver variableResolver = mapBasedValueResolver.with("a", Value.of(BigDecimal.valueOf(9)));
		Object result = createExpression("a").evaluate(variableResolver);
		assertThat(result).isEqualTo(Value.of(BigDecimal.valueOf(9)));
	}

	@Test
	void testNumbers() throws ParseException, EvaluationException {
		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Value.of(BigDecimal.valueOf(9)));
		VariableResolver variableResolver = mapBasedValueResolver1.with("b", Value.of(BigDecimal.valueOf(5)));
		Object result = createExpression("(a+b)*(a-b)").evaluate(variableResolver);
		assertThat(result).isEqualTo(Value.of(BigDecimal.valueOf(56)));
	}

	@Test
	void testStrings() throws ParseException, EvaluationException {
        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
        MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver.with("prefix", Value.of("Hello"));
        MapBasedVariableResolver mapBasedValueResolver2 = mapBasedValueResolver1.with("infix", Value.of(" "));
        VariableResolver variableResolver = mapBasedValueResolver2.with("postfix", Value.of("world"));
		Object result = createExpression("prefix+infix+postfix").evaluate(variableResolver);
		assertThat(result).isEqualTo(Value.of("Hello world"));
	}

	@Test
	void testStringNumberCombined() throws ParseException, EvaluationException {
        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver2 = mapBasedValueResolver.with("prefix", Value.of("Hello"));
		MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver2.with("infix", Value.of(BigDecimal.valueOf(2)));
        VariableResolver variableResolver = mapBasedValueResolver1.with("postfix", Value.of("world"));
		Object result = createExpression("prefix+infix+postfix").evaluate(variableResolver);
		assertThat(result).isEqualTo(Value.of("Hello2world"));
	}

	@Test
	void testUnknownVariable() {
		assertThatThrownBy(() -> createExpression("a").evaluate(VariableResolver.empty()))
			.isInstanceOf(EvaluationException.class)
			.hasMessage("Variable or constant value for 'a' not found");
	}
}
