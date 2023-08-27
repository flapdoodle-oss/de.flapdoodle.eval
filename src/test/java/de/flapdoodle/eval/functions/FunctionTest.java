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
package de.flapdoodle.eval.functions;

import de.flapdoodle.eval.*;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FunctionTest {

	@Test
	void testParameterDefinition() {
		Evaluateable function = new CorrectFunctionDefinitionFunction();

		assertThat(function.parameters().get(0).isLazy()).isFalse();
		assertThat(function.parameters().get(1).isLazy()).isTrue();
		assertThat(function.parameters().get(2).isLazy()).isFalse();
		assertThat(function.parameters().isVarArg()).isTrue();
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

	private static class CorrectFunctionDefinitionFunction extends Evaluateables.Base {

		protected CorrectFunctionDefinitionFunction() {
			super(
				Parameters.varArgWith(
				Parameter.of(Value.class),
				Parameter.lazyWith(Value.class),
					Parameter.of(Value.class)
				)
			);
		}

		@Override
		protected Value<?> evaluateValidated(ValueResolver variableResolver, EvaluationContext evaluationContext, CommonToken functionToken, List<Value<?>> arguments)
			throws EvaluationException {
			return Value.of("OK");
		}
	}

}
