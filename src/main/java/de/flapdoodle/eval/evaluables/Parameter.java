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
package de.flapdoodle.eval.evaluables;

import de.flapdoodle.eval.exceptions.EvaluableException;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.evaluables.validation.ParameterValidator;
import de.flapdoodle.eval.parser.ASTNode;
import de.flapdoodle.eval.parser.Token;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

/** Definition of a function parameter. */
@Value.Immutable
public interface Parameter<T extends de.flapdoodle.eval.values.Value<?>> {

	@Value.Parameter
	Class<T> type();

	/**
	 * Set to true, the parameter will not be evaluated in advance, but the corresponding {@link
	 * ASTNode} will be passed as a parameter value.
	 *
	 * @see de.flapdoodle.eval.evaluables.basic.Conditional for an example.
	 */
	@Value.Default
	default boolean isLazy() {
		return false;
	}

	List<ParameterValidator<T>> validators();

	static <T extends de.flapdoodle.eval.values.Value<?>> ImmutableParameter<T> of(Class<T> type) {
		return ImmutableParameter.of(type);
	}

	static <T extends de.flapdoodle.eval.values.Value<?>> ImmutableParameter<T> lazyWith(Class<T> type) {
		return ImmutableParameter.of(type).withIsLazy(true);
	}

	static ImmutableParameter<de.flapdoodle.eval.values.Value<?>> anyLazy() {
		return ImmutableParameter.of((Class) de.flapdoodle.eval.values.Value.class)
			.withIsLazy(true);
	}

	@Value.Auxiliary
	@Deprecated
	default void validate(Token token, de.flapdoodle.eval.values.Value<?> parameterValue) throws EvaluationException {
		Optional<EvaluableException> error = validationError(parameterValue);
		if (error.isPresent()) {
			throw new EvaluationException(token, error.get());
		}
	}

	@Value.Auxiliary
	default Optional<EvaluableException> validationError(Object parameterValue) {
		if (type().isInstance(parameterValue)) {
			T value = type().cast(parameterValue);
			for (ParameterValidator<T> validator : validators()) {
				Optional<EvaluableException> error = validator.validate(value);
				if (error.isPresent()) {
					return error;
				}
			}
		} else {
			return Optional.of(EvaluableException.ofUnsupportedDataTypeInOperation());
		}
		return Optional.empty();
	}
}