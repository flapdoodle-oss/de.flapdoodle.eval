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
package de.flapdoodle.eval.functions;

import de.flapdoodle.eval.Evaluateable;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.Parameter;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FunctionTest {

  @Test
  void testParameterDefinition() {
    Evaluateable function = new CorrectFunctionDefinitionFunction();

    assertThat(function.parameters().get(0).name()).isEqualTo("default");
    assertThat(function.parameters().get(0).isLazy()).isFalse();
    assertThat(function.parameters().get(0).isVarArg()).isFalse();

    assertThat(function.parameters().get(1).name()).isEqualTo("lazy");
    assertThat(function.parameters().get(1).isLazy()).isTrue();
    assertThat(function.parameters().get(1).isVarArg()).isFalse();

    assertThat(function.parameters().get(2).name()).isEqualTo("vararg");
    assertThat(function.parameters().get(2).isLazy()).isFalse();
    assertThat(function.parameters().get(2).isVarArg()).isTrue();
  }

  @Test
  void testParameterIsLazy() {
    Evaluateable function = new CorrectFunctionDefinitionFunction();

    assertThat(function.parameterIsLazy(0)).isFalse();
    assertThat(function.parameterIsLazy(1)).isTrue();
    assertThat(function.parameterIsLazy(2)).isFalse();
    assertThat(function.parameterIsLazy(3)).isFalse();
    assertThat(function.parameterIsLazy(4)).isFalse();
  }

  @Test
  void testVarargNotAllowed() {
    assertThatThrownBy(WrongVarargFunctionDefinitionFunction::new)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Only last parameter may be defined as variable argument");
  }

  private static class CorrectFunctionDefinitionFunction extends AbstractFunction {

    protected CorrectFunctionDefinitionFunction() {
      super(
        Parameter.of(Value.class,"default"),
        Parameter.lazyWith(Value.class,"lazy"),
        Parameter.varArgWith(Value.class,"vararg")
      );
    }

    @Override public Value<?> evaluateValidated(ValueResolver variableResolver, Expression expression, Token functionToken, List<Value<?>> arguments)
      throws EvaluationException {
      return Value.of("OK");
    }
  }

  private static class WrongVarargFunctionDefinitionFunction extends AbstractFunction {
    public WrongVarargFunctionDefinitionFunction() {
      super(
        Parameter.of(Value.class,"default"),
        Parameter.varArgWith(Value.class, "vararg"),
        Parameter.of(Value.class,"another")
      );
    }
    @Override public Value<?> evaluateValidated(ValueResolver variableResolver, Expression expression, Token functionToken, List<Value<?>> arguments)
      throws EvaluationException {
      return Value.of("OK");
    }
  }
}
