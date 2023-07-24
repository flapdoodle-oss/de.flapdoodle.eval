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
package de.flapdoodle.eval.functions.basic;

import de.flapdoodle.eval.Evaluateables;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.Parameter;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.math.BigDecimal;
import java.util.List;

public class Max extends Evaluateables.SingleVararg<Value.NumberValue> {

	public Max() {
		super(Parameter.of(Value.NumberValue.class));
	}

	@Override
	protected Value<?> evaluateVarArg(ValueResolver variableResolver, EvaluationContext evaluationContext, Token functionToken,
		List<Value.NumberValue> parameterValues) {
		BigDecimal max = null;
		for (Value.NumberValue parameter : parameterValues) {
			if (max == null || parameter.wrapped().compareTo(max) > 0) {
				max = parameter.wrapped();
			}
		}
		return Value.of(max);
	}
}
