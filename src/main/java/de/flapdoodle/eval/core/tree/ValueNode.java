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
package de.flapdoodle.eval.core.tree;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.Evaluable;
import de.flapdoodle.eval.core.evaluables.Evaluated;
import de.flapdoodle.eval.core.parser.Token;

@org.immutables.value.Value.Immutable
public abstract class ValueNode<T> extends Node {
	@org.immutables.value.Value.Parameter
	protected abstract T value();

	@Override
	public Evaluated<?> evaluate(VariableResolver variableResolver, EvaluationContext context) {
		return Evaluated.value(value());
	}

	public static <T> ValueNode<T> of(Token token, T value) {
		return ImmutableValueNode.<T>builder()
			.token(token)
			.value(value)
			.build();
	}
}
