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

import de.flapdoodle.eval.CommonToken;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

public abstract class AbstractBaseOperator implements Operator {

	private final int precedence;
	private final boolean leftAssociative;

	protected AbstractBaseOperator(int precedence, boolean leftAssociative) {
		this.precedence = precedence;
		this.leftAssociative = leftAssociative;
	}

	protected AbstractBaseOperator(Precedence precedence, boolean leftAssociative) {
		this(precedence.value(), leftAssociative);
	}

	protected AbstractBaseOperator(Precedence precedence) {
		this(precedence, true);
	}

	@Override
	public int getPrecedence() {
		return precedence;
	}

	@Override
	public boolean isLeftAssociative() {
		return leftAssociative;
	}

	protected static <T extends Value<?>> T requireValueType(CommonToken operatorToken, Value<?> value, Class<T> type) throws EvaluationException {
		if (type.isInstance(value)) {
			return type.cast(value);
		}
		throw new EvaluationException(operatorToken, "type missmatch: " + value + " is not a " + type);
	}

}
