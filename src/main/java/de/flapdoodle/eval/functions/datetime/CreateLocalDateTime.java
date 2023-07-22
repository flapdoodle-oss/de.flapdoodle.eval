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
package de.flapdoodle.eval.functions.datetime;

import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.functions.AbstractFunction;
import de.flapdoodle.eval.functions.FunctionParameterDefinition;
import de.flapdoodle.eval.functions.validations.NonNegativeNumber;
import de.flapdoodle.eval.parser.Token;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

// das ist eigentlich 3+vararg
public class CreateLocalDateTime extends AbstractFunction.SingleVararg<Value.NumberValue> {
  public CreateLocalDateTime() {
    super(FunctionParameterDefinition.varArgWith(Value.NumberValue.class,"values")
      .withValidators(new NonNegativeNumber()));
  }
  @Override public Value<?> evaluateVarArg(ValueResolver variableResolver, Expression expression, Token functionToken,
    List<Value.NumberValue> parameterValues) {
    int year = parameterValues.get(0).wrapped().intValue();
    int month = parameterValues.get(1).wrapped().intValue();
    int day = parameterValues.get(2).wrapped().intValue();
    int hour = parameterValues.size() >= 4 ? parameterValues.get(3).wrapped().intValue() : 0;
    int minute = parameterValues.size() >= 5 ? parameterValues.get(4).wrapped().intValue() : 0;
    int second = parameterValues.size() >= 6 ? parameterValues.get(5).wrapped().intValue() : 0;
    int nanoOfs = parameterValues.size() >= 7 ? parameterValues.get(6).wrapped().intValue() : 0;

    ZoneId zoneId = expression.getConfiguration().getDefaultZoneId();
    return Value.of(
        LocalDateTime.of(year, month, day, hour, minute, second, nanoOfs)
            .atZone(zoneId)
            .toInstant());
  }
}
