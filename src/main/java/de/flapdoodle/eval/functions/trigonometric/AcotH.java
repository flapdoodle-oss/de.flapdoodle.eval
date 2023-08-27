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

import de.flapdoodle.eval.CommonToken;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;

public class AcotH extends AbstractNumberFunction {

	@Override
	protected Value<?> evaluate(ValueResolver variableResolver, EvaluationContext evaluationContext, CommonToken functionToken,
		Value.NumberValue parameterValue) throws EvaluationException {
		/* Formula: acoth(x) = log((x + 1) / (x - 1)) * 0.5 */
		double value = parameterValue.wrapped().doubleValue();
		return Value.of(Math.log((value + 1) / (value - 1)) * 0.5);
	}
}
