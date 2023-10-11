package de.flapdoodle.eval.evaluables.arithmetic;

import de.flapdoodle.eval.exceptions.EvaluableException;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluableByArguments;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.parser.TokenType;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.types.Either;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.MathContext;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static de.flapdoodle.eval.AssertEither.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class MinusTest {
	private Minus testee = new Minus();

	private EvaluationContext evaluationContext = EvaluationContext.builder()
		.zoneId(ZoneId.systemDefault())
		.mathContext(MathContext.UNLIMITED)
		.build();

	private Token token = Token.of(0,"<foo>", TokenType.VARIABLE_OR_CONSTANT);

	private static Stream<Arguments> validSamples() {
		ZonedDateTime now= ZonedDateTime.now();

		return Stream.of(
			Arguments.of(Value.of(5.0), Value.of(3.8), Value.of(1.2)),
			Arguments.of(Value.of(now.toInstant()), Value.of(Duration.ofDays(1)), Value.of(now.minusDays(1).toInstant())),
			Arguments.of(Value.of(now.toInstant()), Value.of(now.minusDays(1).toInstant()), Value.of(Duration.ofDays(1))),
			Arguments.of(Value.of(Duration.ofDays(4)), Value.of(Duration.ofDays(2)), Value.of(Duration.ofDays(2))),
			Arguments.of(Value.of(now.toInstant()), Value.of(1000), Value.of(now.minusSeconds(1).toInstant()))
		);
	}

	@ParameterizedTest
	@MethodSource("validSamples")
	void add(Value<?> first, Value<?> second, Value<?> expected) throws EvaluationException {
		List<Value<? extends Object>> values = Arrays.asList(first, second);

		Optional<? extends TypedEvaluableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(values.size());
		assertThat(byNumberOfArguments).isPresent();

		Either<TypedEvaluable<?>, List<EvaluableException>> byArguments = byNumberOfArguments.get().find(values);
		assertThat(byArguments).isLeft();

		Value<?> result = byArguments.left()
			.evaluate(ValueResolver.empty(), evaluationContext, token, values);

		assertThat(result).isEqualTo(expected);
	}

	@Test
	void negate() throws EvaluationException {
		List<Value<? extends Object>> values = Arrays.asList(Value.of(123.0));

		Optional<? extends TypedEvaluableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(values.size());
		assertThat(byNumberOfArguments).isPresent();

		Either<TypedEvaluable<?>, List<EvaluableException>> byArguments = byNumberOfArguments.get().find(values);
		assertThat(byArguments).isLeft();

		Value<?> result = byArguments.left()
			.evaluate(ValueResolver.empty(), evaluationContext, token, values);

		assertThat(result).isEqualTo(Value.of(-123.0));
	}
}