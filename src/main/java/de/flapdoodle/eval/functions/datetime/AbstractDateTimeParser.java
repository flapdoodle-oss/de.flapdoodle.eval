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
/*
  Copyright 2012-2023 Udo Klimaschewski

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package de.flapdoodle.eval.functions.datetime;

import de.flapdoodle.eval.Evaluateables;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.Parameter;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

public abstract class AbstractDateTimeParser extends Evaluateables.SingleVararg<Value.StringValue> {
	protected AbstractDateTimeParser() {
		super(Parameter.varArgWith(Value.StringValue.class, "value"));
	}

	@Override
	protected Value<?> evaluateVarArg(ValueResolver variableResolver, EvaluationContext evaluationContext, Token functionToken,
		List<Value.StringValue> parameterValues) {
		ZoneId zoneId = evaluationContext.zoneId();
		Instant instant;

		if (parameterValues.size() < 2) {
			instant = parse(parameterValues.get(0).wrapped(), null, zoneId);
		} else {
			instant =
				parse(parameterValues.get(0).wrapped(), parameterValues.get(1).wrapped(), zoneId);
		}
		return Value.of(instant);
	}

	protected abstract Instant parse(String value, String format, ZoneId zoneId);
}
