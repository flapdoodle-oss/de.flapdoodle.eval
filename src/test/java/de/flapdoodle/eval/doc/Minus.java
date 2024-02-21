/*
 * Copyright (C) 2023
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.eval.doc;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;

public class Minus extends TypedEvaluables.Wrapper {

	public static class Int implements TypedEvaluable.Arg2<Integer, Integer, Integer> {

		@Override
		public Integer evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Integer first,
			Integer second) throws EvaluationException {
			return first - second;
		}
	}

	public static class Negate implements TypedEvaluable.Arg1<Integer, Integer> {

		@Override
		public Integer evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Integer first) throws EvaluationException {
			return -first;
		}
	}

	public Minus() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(Integer.class, Integer.class, Integer.class, new Int()))
			.addList(TypedEvaluable.of(Integer.class, Integer.class, new Negate()))
			.build());
	}
}
