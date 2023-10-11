package de.flapdoodle.eval.evaluables.arithmetic;

import de.flapdoodle.eval.EvaluableException;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluableByArguments;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.parser.TokenType;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.types.Either;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.math.MathContext;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.flapdoodle.eval.AssertEither.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LogTest {
	private Log testee = new Log();

	private EvaluationContext evaluationContext = EvaluationContext.builder()
		.zoneId(ZoneId.systemDefault())
		.mathContext(MathContext.UNLIMITED)
		.build();

	private Token token = Token.of(0, "<foo>", TokenType.VARIABLE_OR_CONSTANT);

	@Test
	void sample() throws EvaluationException {
		List<Value<? extends Object>> values = Arrays.asList(Value.of(3.0));

		Optional<? extends TypedEvaluableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(values.size());
		assertThat(byNumberOfArguments).isPresent();

		Either<TypedEvaluable<?>, List<EvaluableException>> byArguments = byNumberOfArguments.get().find(values);
		assertThat(byArguments).isLeft();

		Value<?> result = byArguments.left()
			.evaluate(ValueResolver.empty(), evaluationContext, token, values);

		assertThat(result).isEqualTo(Value.of(1.0986122886681098));
	}

	@Test
	void failOnNegative() {
		Optional<? extends TypedEvaluableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(1);
		assertThat(byNumberOfArguments).isPresent();

		assertThat(byNumberOfArguments.get().find(Arrays.asList(Value.of(-1.0))))
			.isRight()
			.rightSatisfies(errors -> assertThat(errors)
				.singleElement(InstanceOfAssertFactories.throwable(EvaluableException.class))
				.hasMessageContaining("value is not > 0: -1.0"));

		Either<TypedEvaluable<?>, List<EvaluableException>> byArguments = byNumberOfArguments.get().find(Arrays.asList(Value.of(1.0)));
		assertThat(byArguments).isLeft();

		assertThatThrownBy(() -> byArguments.left()
			.evaluate(ValueResolver.empty(), evaluationContext, token, Arrays.asList(Value.of(-1.0))))
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value is not > 0: -1.0");
	}

	@Test
	void failOnZero() {
		Optional<? extends TypedEvaluableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(1);
		assertThat(byNumberOfArguments).isPresent();

		assertThat(byNumberOfArguments.get().find(Arrays.asList(Value.of(0.0))))
			.isRight()
			.rightSatisfies(errors -> assertThat(errors)
				.singleElement(InstanceOfAssertFactories.throwable(EvaluableException.class))
				.hasMessageContaining("value is not > 0: 0.0"));

		Either<TypedEvaluable<?>, List<EvaluableException>> byArguments = byNumberOfArguments.get().find(Arrays.asList(Value.of(1.0)));
		assertThat(byArguments).isLeft();

		assertThatThrownBy(() -> byArguments.left()
			.evaluate(ValueResolver.empty(), evaluationContext, token, Arrays.asList(Value.of(0.0))))
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value is not > 0: 0.0");
	}
}