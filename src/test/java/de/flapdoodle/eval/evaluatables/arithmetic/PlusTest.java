package de.flapdoodle.eval.evaluatables.arithmetic;

import de.flapdoodle.eval.EvaluatableException;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatableByArguments;
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

class PlusTest {
	private Plus testee = new Plus();

	private EvaluationContext evaluationContext = EvaluationContext.builder()
		.zoneId(ZoneId.systemDefault())
		.mathContext(MathContext.UNLIMITED)
		.build();

	private Token token = Token.of(0,"<foo>", TokenType.VARIABLE_OR_CONSTANT);

	private static Stream<Arguments> validSamples() {
		ZonedDateTime now= ZonedDateTime.now();

		return Stream.of(
			Arguments.of(Value.of(1.2), Value.of(3.8), Value.of(5.0)),
			Arguments.of(Value.of(now.toInstant()), Value.of(Duration.ofDays(1)), Value.of(now.plusDays(1).toInstant())),
			Arguments.of(Value.of(Duration.ofDays(2)), Value.of(Duration.ofDays(2)), Value.of(Duration.ofDays(4))),
			Arguments.of(Value.of(now.toInstant()), Value.of(1000), Value.of(now.plusSeconds(1).toInstant())),
			Arguments.of(Value.of(1.2), Value.of(false), Value.of("1.2false"))
		);
	}

	@ParameterizedTest
	@MethodSource("validSamples")
	void add(Value<?> first, Value<?> second, Value<?> expected) throws EvaluationException {
		List<Value<? extends Object>> values = Arrays.asList(first, second);

		Optional<? extends TypedEvaluatableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(values.size());
		assertThat(byNumberOfArguments).isPresent();

		Either<TypedEvaluatable<?>, List<EvaluatableException>> byArguments = byNumberOfArguments.get().find(values);
		assertThat(byArguments).isLeft();

		Value<?> result = byArguments.left()
			.evaluate(ValueResolver.empty(), evaluationContext, token, values);

		assertThat(result).isEqualTo(expected);
	}

	@Test
	void prefixPlus() throws EvaluationException {
		List<Value<? extends Object>> values = Arrays.asList(Value.of(-123.0));

		Optional<? extends TypedEvaluatableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(values.size());
		assertThat(byNumberOfArguments).isPresent();

		Either<TypedEvaluatable<?>, List<EvaluatableException>> byArguments = byNumberOfArguments.get().find(values);
		assertThat(byArguments).isLeft();

		Value<?> result = byArguments.left()
			.evaluate(ValueResolver.empty(), evaluationContext, token, values);

		assertThat(result).isEqualTo(Value.of(-123.0));
	}

	@Test
	void sum() throws EvaluationException {
		List<Value<? extends Object>> values = Arrays.asList(Value.of(1.0), Value.of(2.0), Value.of(3.0));

		Optional<? extends TypedEvaluatableByArguments> byNumberOfArguments = testee.filterByNumberOfArguments(values.size());
		assertThat(byNumberOfArguments).isPresent();

		Either<TypedEvaluatable<?>, List<EvaluatableException>> byArguments = byNumberOfArguments.get().find(values);
		assertThat(byArguments).isLeft();

		Value<?> result = byArguments.left()
			.evaluate(ValueResolver.empty(), evaluationContext, token, values);

		assertThat(result).isEqualTo(Value.of(6.0));
	}
}