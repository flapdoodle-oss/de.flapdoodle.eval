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

import de.flapdoodle.eval.config.*;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.InfixOperator;
import de.flapdoodle.eval.operators.Operator;
import de.flapdoodle.eval.operators.arithmetic.Plus;
import de.flapdoodle.types.Pair;
import org.junit.jupiter.api.Test;

import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ExpressionFactoryTest {

	@Test
	void testDefaultSetup() {
		ExpressionFactory factory = ExpressionFactory.defaults();

		assertThat(factory.mathContext())
			.isSameAs(Defaults.mathContext());
		assertThat(factory.operators())
			.isSameAs(Defaults.operators());
		assertThat(factory.functions())
			.isSameAs(Defaults.functions());
		assertThat(factory.constants())
			.isSameAs(Defaults.constants());
	}

	@SuppressWarnings("unchecked")
	@Test
	void testWithAdditionalOperators() {
		ExpressionFactory factory =
			ExpressionFactory.defaults()
				.withOperators(
					Pair.of("ADDED1", new Plus()),
					Pair.of("ADDED2", new Plus()));

		assertThat(factory.operators().hasOperator(InfixOperator.class, "ADDED1")).isTrue();
		assertThat(factory.operators().hasOperator(InfixOperator.class, "ADDED2")).isTrue();
	}

	@Test
	void testWithAdditionalFunctions() {
		ExpressionFactory factory = ImmutableExpressionFactory.copyOf(ExpressionFactory.defaults())
			.withFunctions(Pair.of("ADDED1", new TestConfigurationProvider.DummyFunction()),
				Pair.of("ADDED2", new TestConfigurationProvider.DummyFunction()));

		assertThat(factory.functions().has("ADDED1")).isTrue();
		assertThat(factory.functions().has("ADDED2")).isTrue();
	}

	@Test
	void testCustomMathContext() {
		ExpressionFactory factory =
			ExpressionFactory.defaults().withMathContext(MathContext.DECIMAL32);

		assertThat(factory.mathContext()).isEqualTo(MathContext.DECIMAL32);
	}

	@Test
	void testCustomOperatorDictionary() {
		OperatorResolver mockedOperatorDictionary = new OperatorResolver() {
			@Override
			public <T extends Operator> T get(Class<T> type, String operatorString) {
				throw new IllegalStateException("dont call this");
			}

			@Override
			public boolean hasStartingWith(Class<? extends Operator> type, String value) {
				throw new IllegalStateException("dont call this");
			}
		};

		ExpressionFactory factory =
			ExpressionFactory.defaults().withOperators(mockedOperatorDictionary);

		assertThat(factory.operators()).isEqualTo(mockedOperatorDictionary);
	}

	@Test
	void testCustomFunctionDictionary() {
		EvaluateableResolver mockedFunctions = name -> {
			throw new IllegalStateException("dont call this");
		};

		ExpressionFactory factory =
			ExpressionFactory.defaults()
				.withFunctions(mockedFunctions);

		assertThat(factory.functions()).isEqualTo(mockedFunctions);
	}

	@Test
	void testCustomConstants() {
		Map<String, Value<?>> constants =
			new HashMap<String, Value<?>>() {
				{
					put("A", Value.of("a"));
					put("B", Value.of("b"));
				}
			};
		MapBasedValueResolver valueResolver = ValueResolver.empty()
			.withValues(constants);
		ExpressionFactory factory =
			ExpressionFactory.defaults().withConstants(valueResolver);

		assertThat(factory.constants().get("a")).isEqualTo(Value.of("a"));
	}
}
