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
import de.flapdoodle.eval.core.validation.ParameterValidator;
import de.flapdoodle.eval.example.Value;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static de.flapdoodle.eval.example.AssertEither.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class TypedEvaluablesTest {
	TypedEvaluable<Value.NumberValue> addOne = TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class,
		(valueResolver, evaluationContext, token, argument) ->
			Value.of(argument.wrapped().add(BigDecimal.ONE)));

	TypedEvaluable<Value.NumberValue> sum2 = TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class,
		(valueResolver, evaluationContext, token, a, b) ->
			Value.of(a.wrapped().add(b.wrapped())));

	TypedEvaluable<Value.NumberValue> negAddOne = TypedEvaluable.of(Value.NumberValue.class,
		Parameter.of(Value.NumberValue.class).withValidators(new ParameterValidator<Value.NumberValue>() {
			@Override
			public Optional<EvaluableException> validate(Value.NumberValue parameterValue) {
				return parameterValue.wrapped().doubleValue() >= 0 ?
					Optional.of(EvaluableException.of("positive number", parameterValue.wrapped())) :
					Optional.empty();
			}
		}),
		(valueResolver, evaluationContext, token, argument) ->
			Value.of(argument.wrapped().add(BigDecimal.ONE)));

	private TypedEvaluables testee = TypedEvaluables.builder()
		.addList(negAddOne)
		.addList(addOne)
		.addList(sum2)
		.build();

	@Test
	void filterByNumberOfArguments() {
		assertThat(testee.filterByNumberOfArguments(1))
			.isPresent()
			.get()
			.asInstanceOf(InstanceOfAssertFactories.type(TypedEvaluables.class))
			.extracting(TypedEvaluables::list, InstanceOfAssertFactories.list(TypedEvaluable.class))
			.containsExactly(negAddOne, addOne);
	}

	@Test
	void filterByArguments() {
		assertThat(testee.find(Collections.singletonList(Value.of(1.23))))
			.isLeft()
			.containsLeft(addOne);

		assertThat(testee.find(Arrays.asList(Value.of(1.23), Value.of(3.45))))
			.isLeft()
			.containsLeft(sum2);

		assertThat(testee.find(Collections.singletonList(Value.of(-1.23))))
			.isLeft()
			.containsLeft(negAddOne);
	}
}