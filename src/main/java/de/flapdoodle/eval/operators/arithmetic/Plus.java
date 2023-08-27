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

import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.AbstractInfixOperator;
import de.flapdoodle.eval.operators.Precedence;

import java.time.Duration;

public class Plus extends AbstractInfixOperator {

	public Plus() {
		super(Precedence.OPERATOR_PRECEDENCE_ADDITIVE);
	}

	@Override
	public Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token operatorToken, Value<?> leftOperand, Value<?> rightOperand) throws EvaluationException {
		return evaluate(operatorToken, leftOperand, rightOperand)
			.using(Value.NumberValue.class, Value.NumberValue.class, (l, r) -> Value.of(l.wrapped().add(r.wrapped(), evaluationContext.mathContext())))
			.using(Value.DateTimeValue.class, Value.DurationValue.class, (l, r) -> Value.of(l.wrapped().plus(r.wrapped())))
			.using(Value.DurationValue.class, Value.DurationValue.class, (l, r) -> Value.of(l.wrapped().plus(r.wrapped())))
			.using(Value.DateTimeValue.class, Value.NumberValue.class, (l, r) -> Value.of(l.wrapped().plus(Duration.ofMillis(r.wrapped().longValue()))))
			.using(Value.class, Value.class, (l, r) -> Value.of(l.wrapped().toString() + r.wrapped()))
			.get();
	}
}
