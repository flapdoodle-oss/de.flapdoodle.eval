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
package de.flapdoodle.eval.operators.arithmetic;

import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.AbstractInfixOperator;
import de.flapdoodle.eval.operators.Precedence;
import de.flapdoodle.eval.parser.Token;

import java.time.Duration;

public class Minus extends AbstractInfixOperator {

	public Minus() {
		super(Precedence.OPERATOR_PRECEDENCE_ADDITIVE);
	}

	@Override
	public Value<?> evaluate(
		Expression expression, Token operatorToken, Value<?> leftOperand, Value<?> rightOperand)
		throws EvaluationException {

		return evaluate(operatorToken, leftOperand, rightOperand)
			.using(Value.NumberValue.class, Value.NumberValue.class,
				(l, r) -> Value.of(l.wrapped().subtract(r.wrapped(), expression.configuration().getMathContext())))
			.using(Value.DateTimeValue.class, Value.DateTimeValue.class,
				(l, r) -> Value.of(Duration.ofMillis(l.wrapped().toEpochMilli() - r.wrapped().toEpochMilli())))
			.using(Value.DateTimeValue.class, Value.DurationValue.class,
				(l, r) -> Value.of(l.wrapped().minus(r.wrapped())))
			.using(Value.DurationValue.class, Value.DurationValue.class,
				(l, r) -> Value.of(l.wrapped().minus(r.wrapped())))
			// TODO remove this
			.using(Value.DateTimeValue.class, Value.NumberValue.class,
				(l, r) -> Value.of(l.wrapped().minus(Duration.ofMillis(r.wrapped().longValue()))))
			.get();
	}
}
