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
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.data.VariableResolver;
import de.flapdoodle.eval.functions.AbstractFunction;
import de.flapdoodle.eval.parser.Token;

import java.math.BigDecimal;
import java.time.Duration;

public class DurationMillisFromNumber extends AbstractFunction.Single<Value.NumberValue> {

  public DurationMillisFromNumber() {
    super(Value.NumberValue.class);
  }

  @Override public Value<?> evaluate(VariableResolver variableResolver, Expression expression, Token functionToken,
    Value.NumberValue parameterValue) {
    BigDecimal millis = parameterValue.wrapped();
    return Value.of(Duration.ofMillis(millis.longValue()));
  }
}
