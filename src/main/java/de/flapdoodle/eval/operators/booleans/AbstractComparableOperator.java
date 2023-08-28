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

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.AbstractInfixOperator;
import de.flapdoodle.eval.operators.Precedence;
import de.flapdoodle.eval.parser.Token;

public abstract class AbstractComparableOperator extends AbstractInfixOperator {

	protected AbstractComparableOperator(Precedence precedence, boolean leftAssociative) {
		super(precedence, leftAssociative);
	}

	protected AbstractComparableOperator(Precedence precedence) {
		super(precedence);
	}

	@Override
	public Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token operatorToken, Value<?> leftOperand, Value<?> rightOperand) throws EvaluationException {
		if (leftOperand.getClass() == rightOperand.getClass()) {
			if (leftOperand instanceof Value.ComparableValue) {
				return evaluateComparable(evaluationContext, operatorToken, (Value.ComparableValue) leftOperand, (Value.ComparableValue) rightOperand);
			}
			throw new EvaluationException(operatorToken, "not comparable: " + leftOperand + ", " + rightOperand);
		}
		throw new EvaluationException(operatorToken, "different types: " + leftOperand + ", " + rightOperand);
	}

	protected abstract <T extends Comparable<T>, V extends Value.ComparableValue<T>> Value<?> evaluateComparable(EvaluationContext evaluationContext, Token operatorToken,
		V leftOperand, V rightOperand);
}
