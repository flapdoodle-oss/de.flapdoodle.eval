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
package de.flapdoodle.eval.config;

import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.functions.Function;
import de.flapdoodle.eval.operators.InfixOperator;
import de.flapdoodle.eval.operators.Operator;
import de.flapdoodle.eval.operators.arithmetic.Plus;
import de.flapdoodle.types.Pair;
import org.junit.jupiter.api.Test;

import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationTest {

  @Test
  void testDefaultSetup() {
    Configuration configuration = Configuration.defaultConfiguration();

    assertThat(configuration.getMathContext())
        .isSameAs(Defaults.mathContext());
    assertThat(configuration.getOperatorResolver())
        .isSameAs(Defaults.operators());
    assertThat(configuration.getFunctionResolver())
        .isSameAs(Defaults.functions());
    assertThat(configuration.isArraysAllowed()).isTrue();
    assertThat(configuration.isStructuresAllowed()).isTrue();
    assertThat(configuration.isImplicitMultiplicationAllowed()).isTrue();
    assertThat(configuration.getConstantResolver())
        .isSameAs(Defaults.constants());
//    assertThat(configuration.getDecimalPlacesRounding())
//        .isEqualTo(Configuration.DECIMAL_PLACES_ROUNDING_UNLIMITED);
//    assertThat(configuration.isStripTrailingZeros()).isTrue();
    assertThat(configuration.isAllowOverwriteConstants()).isTrue();
  }

  @SuppressWarnings("unchecked") @Test
  void testWithAdditionalOperators() {
    Configuration configuration =
        Configuration.defaultConfiguration()
            .withOperators(
                Pair.of("ADDED1", new Plus()),
                Pair.of("ADDED2", new Plus()));

    assertThat(configuration.getOperatorResolver().hasOperator(InfixOperator.class, "ADDED1")).isTrue();
    assertThat(configuration.getOperatorResolver().hasOperator(InfixOperator.class, "ADDED2")).isTrue();
  }

  @Test
  void testWithAdditionalFunctions() {
      Configuration configuration1 = Configuration.defaultConfiguration();
      Pair<String, Function>[] functions = new Pair[]{Pair.of("ADDED1", new TestConfigurationProvider.DummyFunction()), Pair.of("ADDED2", new TestConfigurationProvider.DummyFunction())};
      Configuration configuration =
              ImmutableConfiguration.copyOf(configuration1)
                      .withFunctionResolver(MapBasedFunctionResolver.of(functions)
                              .andThen(configuration1.getFunctionResolver()));

    assertThat(configuration.getFunctionResolver().hasFunction("ADDED1")).isTrue();
    assertThat(configuration.getFunctionResolver().hasFunction("ADDED2")).isTrue();
  }

  @Test
  void testCustomMathContext() {
    Configuration configuration =
        Configuration.builder().mathContext(MathContext.DECIMAL32).build();

    assertThat(configuration.getMathContext()).isEqualTo(MathContext.DECIMAL32);
  }

  @Test
  void testCustomOperatorDictionary() {
    OperatorResolver mockedOperatorDictionary = new OperatorResolver() {
      @Override
      public <T extends Operator> T get(Class<T> type, String operatorString) {
        throw new IllegalStateException("dont call this");
      }
    };

    Configuration configuration =
        Configuration.builder().operatorResolver(mockedOperatorDictionary).build();

    assertThat(configuration.getOperatorResolver()).isEqualTo(mockedOperatorDictionary);
  }

  @Test
  void testCustomFunctionDictionary() {
    FunctionResolver mockedFunctionDictionary = functionName -> {
      throw new IllegalStateException("dont call this");
    };

    Configuration configuration =
        Configuration.builder().functionResolver(mockedFunctionDictionary).build();

    assertThat(configuration.getFunctionResolver()).isEqualTo(mockedFunctionDictionary);
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
    MapBasedValueResolver mapBasedVariableResolver = ValueResolver.empty()
      .withValues(constants);
    Configuration configuration =
        Configuration.builder().constantResolver(mapBasedVariableResolver).build();

    assertThat(configuration.getConstantResolver().get("a")).isEqualTo(Value.of("a"));
  }
}
