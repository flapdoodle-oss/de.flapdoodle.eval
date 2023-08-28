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
package de.flapdoodle.eval.operators;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

public abstract class AbstractInfixOperator extends AbstractBaseOperator implements InfixOperator {
	protected AbstractInfixOperator(Precedence precedence, boolean leftAssociative) {
		super(precedence, leftAssociative);
	}

	protected AbstractInfixOperator(Precedence precedence) {
		super(precedence);
	}

	protected static Evaluator evaluate(Token operatorToken, Value<?> leftOperand, Value<?> rightOperand) {
		return new Evaluator(operatorToken, leftOperand, rightOperand);
	}

	public static abstract class Typed<L extends Value<?>, R extends Value<?>> extends AbstractInfixOperator {

		private final Class<L> leftType;
		private final Class<R> rightType;

		protected Typed(Precedence precedence, boolean leftAssociative, Class<L> leftType, Class<R> rightType) {
			super(precedence, leftAssociative);
			this.leftType = leftType;
			this.rightType = rightType;
		}

		protected Typed(Precedence precedence, Class<L> leftType, Class<R> rightType) {
			super(precedence);
			this.leftType = leftType;
			this.rightType = rightType;
		}

		@Override
		public final Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token operatorToken, Value<?> leftOperand, Value<?> rightOperand) throws EvaluationException {
			return evaluateTyped(evaluationContext, operatorToken, requireValueType(operatorToken, leftOperand, leftType),
				requireValueType(operatorToken, rightOperand, rightType));
		}

		protected abstract Value<?> evaluateTyped(EvaluationContext evaluationContext, Token operatorToken, L leftOperand, R rightOperand) throws EvaluationException;
	}
}
