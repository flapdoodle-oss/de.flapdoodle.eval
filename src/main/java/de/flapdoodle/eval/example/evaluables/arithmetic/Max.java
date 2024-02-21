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
package de.flapdoodle.eval.example.evaluables.arithmetic;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.example.Value;

import java.math.BigDecimal;
import java.util.List;

public class Max extends TypedEvaluables.Wrapper {

	public static class Number implements TypedEvaluable.VarArg1<Value.NumberValue, Value.NumberValue> {
		@Override
		public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, List<Value.NumberValue> arguments) throws EvaluationException {
			BigDecimal max = null;
			for (Value.NumberValue parameter : arguments) {
				if (max == null || parameter.wrapped().compareTo(max) > 0) {
					max = parameter.wrapped();
				}
			}
			return Value.of(max);
		}
	}

	public Max() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.ofVarArg(Value.NumberValue.class, Value.NumberValue.class, new Number()))
			.build());
	}
}
