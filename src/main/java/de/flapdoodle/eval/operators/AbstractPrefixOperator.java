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

public abstract class AbstractPrefixOperator extends AbstractBaseOperator implements PrefixOperator {
	protected AbstractPrefixOperator(Precedence precedence, boolean leftAssociative) {
		super(precedence, leftAssociative);
	}

	protected AbstractPrefixOperator(Precedence precedence) {
		super(precedence);
	}

	public static abstract class Typed<L extends Value<?>> extends AbstractPrefixOperator {
		private final Class<L> type;

		protected Typed(Precedence precedence, boolean leftAssociative, Class<L> type) {
			super(precedence, leftAssociative);
			this.type = type;
		}

		protected Typed(Precedence precedence, Class<L> type) {
			super(precedence);
			this.type = type;
		}

		@Override
		public final Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token operatorToken, Value<?> operand) throws EvaluationException {
			return evaluateTyped(evaluationContext, operatorToken, requireValueType(operatorToken, operand, type));
		}

		protected abstract Value<?> evaluateTyped(EvaluationContext evaluationContext, Token operatorToken, L operand) throws EvaluationException;
	}
}
