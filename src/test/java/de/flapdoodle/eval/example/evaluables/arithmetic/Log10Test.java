package de.flapdoodle.eval.example.evaluables.arithmetic;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluableByArguments;
import de.flapdoodle.eval.core.exceptions.EvaluableException;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.core.parser.TokenType;
import de.flapdoodle.eval.example.Value;
import de.flapdoodle.types.Either;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.math.MathContext;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.flapdoodle.eval.example.AssertEither.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Log10Test {
	private Log10 testee = new Log10();

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

		Object result = byArguments.left()
			.evaluate(VariableResolver.empty(), evaluationContext, token, values);

		assertThat(result).isEqualTo(Value.of(0.47712125471966244));
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
			.evaluate(VariableResolver.empty(), evaluationContext, token, Arrays.asList(Value.of(-1.0))))
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
			.evaluate(VariableResolver.empty(), evaluationContext, token, Arrays.asList(Value.of(0.0))))
			.isInstanceOf(EvaluationException.class)
			.hasMessage("value is not > 0: 0.0");
	}
}