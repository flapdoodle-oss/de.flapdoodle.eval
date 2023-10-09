package de.flapdoodle.eval.evaluatables;

import de.flapdoodle.eval.EvaluatableException;
import de.flapdoodle.eval.evaluatables.validation.ParameterValidator;
import de.flapdoodle.eval.values.Value;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static de.flapdoodle.eval.AssertEither.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class TypedEvaluatablesTest {
	TypedEvaluatable<Value.NumberValue> addOne = TypedEvaluatable.of(Value.NumberValue.class, Value.NumberValue.class,
		(valueResolver, evaluationContext, token, argument) ->
			Value.of(argument.wrapped().add(BigDecimal.ONE)));

	TypedEvaluatable<Value.NumberValue> sum2 = TypedEvaluatable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class,
		(valueResolver, evaluationContext, token, a, b) ->
			Value.of(a.wrapped().add(b.wrapped())));

	TypedEvaluatable<Value.NumberValue> negAddOne = TypedEvaluatable.of(Value.NumberValue.class,
		Parameter.of(Value.NumberValue.class).withValidators(new ParameterValidator<Value.NumberValue>() {
			@Override
			public Optional<EvaluatableException> validate(Value.NumberValue parameterValue) {
				return parameterValue.wrapped().doubleValue() >= 0 ?
					Optional.of(EvaluatableException.of("positive number", parameterValue.wrapped())) :
					Optional.empty();
			}
		}),
		(valueResolver, evaluationContext, token, argument) ->
			Value.of(argument.wrapped().add(BigDecimal.ONE)));

	private TypedEvaluatables testee = TypedEvaluatables.builder()
		.addList(negAddOne)
		.addList(addOne)
		.addList(sum2)
		.build();

	@Test
	void filterByNumberOfArguments() {
		assertThat(testee.filterByNumberOfArguments(1))
			.isPresent()
			.get()
			.asInstanceOf(InstanceOfAssertFactories.type(TypedEvaluatables.class))
			.extracting(TypedEvaluatables::list, InstanceOfAssertFactories.list(TypedEvaluatable.class))
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