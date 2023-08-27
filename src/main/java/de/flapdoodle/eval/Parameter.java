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
package de.flapdoodle.eval;

import de.flapdoodle.eval.functions.basic.Conditional;
import de.flapdoodle.eval.functions.validations.ParameterValidator;
import de.flapdoodle.eval.parser.ASTNode;
import org.immutables.value.Value;

import java.util.List;

/** Definition of a function parameter. */
@Value.Immutable
public interface Parameter<T extends de.flapdoodle.eval.data.Value<?>> {

	@Value.Parameter
	Class<T> type();

	/**
	 * Set to true, the parameter will not be evaluated in advance, but the corresponding {@link
	 * ASTNode} will be passed as a parameter value.
	 *
	 * @see Conditional for an example.
	 */
	@Value.Default
	default boolean isLazy() {
		return false;
	}

	List<ParameterValidator<T>> validators();

	static <T extends de.flapdoodle.eval.data.Value<?>> ImmutableParameter<T> of(Class<T> type) {
		return ImmutableParameter.of(type);
	}

	static <T extends de.flapdoodle.eval.data.Value<?>> ImmutableParameter<T> lazyWith(Class<T> type) {
		return ImmutableParameter.of(type).withIsLazy(true);
	}

	@Value.Auxiliary
	default void validate(CommonToken token, de.flapdoodle.eval.data.Value<?> parameterValue) throws EvaluationException {
		if (type().isInstance(parameterValue)) {
			T value = type().cast(parameterValue);
			for (ParameterValidator<T> validator : validators()) {
				validator.validate(token, value);
			}
		} else {
			throw EvaluationException.ofUnsupportedDataTypeInOperation(token);
		}
	}
}
