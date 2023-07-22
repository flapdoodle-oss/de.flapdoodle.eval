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
import de.flapdoodle.eval.operators.AbstractPrefixOperator;
import de.flapdoodle.eval.operators.Precedence;
import de.flapdoodle.eval.parser.Token;

public class PrefixMinus extends AbstractPrefixOperator.Typed<Value.NumberValue> {

	public PrefixMinus() {
		super(Precedence.OPERATOR_PRECEDENCE_UNARY, false, Value.NumberValue.class);
	}

	@Override
	protected Value<?> evaluateTyped(Expression expression, Token operatorToken, Value.NumberValue operand) throws EvaluationException {
		return Value.of(operand.wrapped().negate(expression.configuration().getMathContext()));
	}
}
