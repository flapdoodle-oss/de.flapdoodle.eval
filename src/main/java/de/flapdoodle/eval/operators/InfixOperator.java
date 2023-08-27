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

import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;

// Binary infix operator, like x+y
public interface InfixOperator extends Operator {
	Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token operatorToken, Value<?> leftOperand, Value<?> rightOperand) throws EvaluationException;

//	static <L extends Value<?>, R extends Value<?>> InfixOperator asOperator(Evaluateables.Tuple<L, R> evaluatable) {
//		return new AbstractInfixOperator(Precedence.OPERATOR_PRECEDENCE_OR, true) {
//			@Override
//			public Value<?> evaluate(Expression expression, Token operatorToken, Value<?> leftOperand, Value<?> rightOperand) throws EvaluationException {
//
//				return evaluatable.evaluate(null, expression, operatorToken, Arrays.asList(leftOperand, rightOperand));
//			}
//		};
//	}
}
