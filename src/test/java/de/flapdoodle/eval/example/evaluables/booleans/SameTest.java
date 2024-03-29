/*
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
package de.flapdoodle.eval.example.evaluables.booleans;

import de.flapdoodle.eval.core.Expression;
import de.flapdoodle.eval.core.MapBasedVariableResolver;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.exceptions.ParseException;
import de.flapdoodle.eval.example.Defaults;
import de.flapdoodle.eval.example.Value;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SameTest {

	@Test
	void testInfixEqualsVariables() throws EvaluationException, ParseException {
		Expression expression = Defaults.expressionFactory()
				.parse("a===b");

		BigDecimal value = new BigDecimal("1.4");

		MapBasedVariableResolver mapBasedValueResolver8 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver9 = mapBasedValueResolver8.with("a", Value.of(value));
		assertThat(expression.evaluate(mapBasedValueResolver9.with("b", Value.of(value)))
			)
			.isEqualTo(Value.TRUE);

		MapBasedVariableResolver mapBasedValueResolver6 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver7 = mapBasedValueResolver6.with("a", Value.of("Hello"));
		assertThat(expression.evaluate(mapBasedValueResolver7.with("b", Value.of("Hello")))
			)
			.isEqualTo(Value.TRUE);

		MapBasedVariableResolver mapBasedValueResolver4 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver5 = mapBasedValueResolver4.with("a", Value.of("Hello"));
		assertThat(expression.evaluate(mapBasedValueResolver5.with("b", Value.of("Goodbye")))
			)
			.isEqualTo(Value.FALSE);

        MapBasedVariableResolver mapBasedValueResolver2 = VariableResolver.empty();
        MapBasedVariableResolver mapBasedValueResolver3 = mapBasedValueResolver2.with("a", Value.of(true));
        assertThat(expression.evaluate(mapBasedValueResolver3.with("b", Value.of(true)))
			)
			.isEqualTo(Value.TRUE);

        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
        MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Value.of(false));
        assertThat(expression.evaluate(mapBasedValueResolver1.with("b", Value.of(true)))
			)
			.isEqualTo(Value.FALSE);
	}

}