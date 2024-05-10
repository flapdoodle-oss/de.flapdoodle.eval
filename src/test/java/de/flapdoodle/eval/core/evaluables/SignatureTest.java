package de.flapdoodle.eval.core.evaluables;

import de.flapdoodle.eval.core.exceptions.EvaluableException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class SignatureTest {
	@Test
	public void checkSignature() {
		Signature<BigDecimal> testee = Signature.of(BigDecimal.class, Parameter.nullableWith(Number.class));

		assertThat(testee.validateArguments(
			Arrays.asList(Evaluated.value(1.9)))
		).isEmpty();

		assertThat(testee.validateArguments(
			Arrays.asList(Evaluated.ofNull(Double.class)))
		).isEmpty();

		assertThat(testee.validateArguments(
			Arrays.asList(Evaluated.value(BigDecimal.ONE)))
		).isEmpty();

		assertThat(testee.validateArguments(
			Arrays.asList(Evaluated.ofNull(BigDecimal.class)))
		).isEmpty();

		assertThat(testee.validateArguments(
			Arrays.asList(Evaluated.value(""))).get()
		).hasMessageContaining("wrong type: ClassTypeInfo{type=class java.lang.Number} != ClassTypeInfo{type=class java.lang.String} ()");

		assertThat(testee.validateArguments(
			Arrays.asList(Evaluated.ofNull(String.class))).get()
		).hasMessageContaining("wrong nullable type: ClassTypeInfo{type=class java.lang.Number} != ClassTypeInfo{type=class java.lang.String}");
	}
}