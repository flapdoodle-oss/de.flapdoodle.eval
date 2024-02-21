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
import de.flapdoodle.eval.core.evaluables.Parameter;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.example.Value;
import de.flapdoodle.eval.example.evaluables.validation.NumberValidator;

import java.math.BigDecimal;

public class Log extends TypedEvaluables.Wrapper {

	public static class Number implements TypedEvaluable.Arg1<Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue argument)
			throws EvaluationException {
			double d = argument.wrapped().doubleValue();

			return Value.of(Math.log(d));
		}
	}

	public Log() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(
				Value.NumberValue.class,
				Parameter.of(Value.NumberValue.class)
					.withValidators(NumberValidator.greaterThan(BigDecimal.ZERO)),
				new Number()))
			.build());
	}
}
