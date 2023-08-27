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
package de.flapdoodle.eval.functions.basic;

import de.flapdoodle.eval.*;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;

/**
 * Conditional evaluation function. If parameter <code>condition</code> is <code>true</code>, the
 * <code>resultIfTrue</code> value is returned, else the <code>resultIfFalse</code> value. <code>
 * resultIfTrue</code> and <code>resultIfFalse</code> are only evaluated (lazily evaluated),
 * <b>after</b> the condition was evaluated.
 */
public class Conditional extends Evaluateables.Triple<Value.BooleanValue, Value.ExpressionValue, Value.ExpressionValue> {

	public Conditional() {
		super(
			Parameter.of(Value.BooleanValue.class),
			Parameter.lazyWith(Value.ExpressionValue.class),
			Parameter.lazyWith(Value.ExpressionValue.class)
		);
	}

	@Override
	protected Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, CommonToken functionToken, Value.BooleanValue condition,
		Value.ExpressionValue ifTrue, Value.ExpressionValue ifFalse) throws EvaluationException {
		if (condition.wrapped()) {
			return evaluationContext.subtreeEvaluator().apply(ifTrue.wrapped());
		} else {
			return evaluationContext.subtreeEvaluator().apply(ifFalse.wrapped());
		}
	}
}
