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
package de.flapdoodle.eval.functions.string;

import de.flapdoodle.eval.*;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;

public class CaseInsensitiveContains extends Evaluateables.Tuple<Value.StringValue, Value.StringValue> {
	public CaseInsensitiveContains() {
		super(
			Parameter.of(Value.StringValue.class),
			Parameter.of(Value.StringValue.class)
		);
	}

	@Override
	protected Value<?> evaluate(ValueResolver variableResolver, EvaluationContext evaluationContext, CommonToken functionToken, Value.StringValue string,
		Value.StringValue substring) throws EvaluationException {
		return Value.of(string.wrapped().toUpperCase().contains(substring.wrapped().toUpperCase()));
	}
}
