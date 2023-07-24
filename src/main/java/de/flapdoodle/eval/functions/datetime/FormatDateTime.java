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
package de.flapdoodle.eval.functions.datetime;

import de.flapdoodle.eval.*;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FormatDateTime extends Evaluateables.Base {

	public FormatDateTime() {
		super(
			Parameters.optionalWith(
				Parameter.of(Value.DateTimeValue.class, "value"),
				Parameter.of(Value.StringValue.class, "format")
			)
		);
	}

	@Override
	protected Value<?> evaluateValidated(ValueResolver variableResolver, EvaluationContext evaluationContext, Token functionToken, List<Value<?>> arguments)
		throws EvaluationException {
		ZoneId zoneId = evaluationContext.zoneId();

		LocalDateTime dateTimeValue = ((Value.DateTimeValue) (arguments.get(0))).wrapped().atZone(zoneId).toLocalDateTime();

		String formatted;
		if (arguments.size() < 2) {
			formatted = dateTimeValue.toString();
		} else {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(((Value.StringValue) arguments.get(1)).wrapped());
			formatted = dateTimeValue.format(formatter);
		}
		return Value.of(formatted);
	}
}
