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
package de.flapdoodle.eval.core.evaluables;

import de.flapdoodle.eval.core.exceptions.EvaluableException;
import de.flapdoodle.types.Either;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class TypedEvaluables implements TypedEvaluableByArguments, TypedEvaluableByNumberOfArguments {
	protected abstract List<TypedEvaluable<?>> list();

	@Override
	@Value.Auxiliary
	public Optional<? extends TypedEvaluableByArguments> filterByNumberOfArguments(int numberOfArguments) {
		List<TypedEvaluable<?>> filtered = list().stream()
			.filter(entry -> entry.signature().minNumberOfArguments() <= numberOfArguments && entry.signature().maxNumberOfArguments() >= numberOfArguments)
			.collect(Collectors.toList());
		return !filtered.isEmpty()
			? Optional.of(builder().list(filtered).build())
			: Optional.empty();
	}

	@Override
	@Value.Auxiliary
	public Either<TypedEvaluable<?>, List<EvaluableException>> find(List<?> values) {
		List<EvaluableException> errors = new ArrayList<>();
		for (TypedEvaluable<?> evaluatable : list()) {
			Optional<EvaluableException> error = evaluatable.signature().validateArguments(values);
			if (error.isPresent()) errors.add(error.get());
			else return Either.left(evaluatable);
		}

		return Either.right(errors);
	}


	public static ImmutableTypedEvaluables.Builder builder() {
		return ImmutableTypedEvaluables.builder();
	}

	public static abstract class Wrapper implements TypedEvaluableByNumberOfArguments {

		private final TypedEvaluables delegate;

		public Wrapper(TypedEvaluables delegate) {
			this.delegate = delegate;
		}

		@Override
		public final Optional<? extends TypedEvaluableByArguments> filterByNumberOfArguments(int numberOfArguments) {
			return delegate.filterByNumberOfArguments(numberOfArguments);
		}
	}
}
