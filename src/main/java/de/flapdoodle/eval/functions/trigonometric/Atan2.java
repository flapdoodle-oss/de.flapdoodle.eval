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
package de.flapdoodle.eval.functions.trigonometric;

import de.flapdoodle.eval.Evaluateables;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.Parameter;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

public class Atan2 extends Evaluateables.Tuple<Value.NumberValue, Value.NumberValue> {

	public Atan2() {
		super(Parameter.of(Value.NumberValue.class, "y"), Parameter.of(Value.NumberValue.class, "x"));
	}

	@Override
	protected Value<?> evaluate(ValueResolver variableResolver, Expression expression, Token functionToken, Value.NumberValue y,
		Value.NumberValue x) throws EvaluationException {
		return Value.of(
			Math.toDegrees(
				Math.atan2(
					y.wrapped().doubleValue(),
					x.wrapped().doubleValue())));
	}
}
