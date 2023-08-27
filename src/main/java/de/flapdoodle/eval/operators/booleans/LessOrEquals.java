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
package de.flapdoodle.eval.operators.booleans;

import de.flapdoodle.eval.CommonToken;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.Precedence;

public class LessOrEquals extends AbstractComparableOperator {

	public LessOrEquals() {
		super(Precedence.OPERATOR_PRECEDENCE_COMPARISON);
	}

	@Override
	protected <T extends Comparable<T>, V extends Value.ComparableValue<T>> Value<?> evaluateComparable(EvaluationContext evaluationContext, CommonToken operatorToken,
		V leftOperand, V rightOperand) {
		return Value.of(leftOperand.compareTo(rightOperand) <= 0);
	}
}
