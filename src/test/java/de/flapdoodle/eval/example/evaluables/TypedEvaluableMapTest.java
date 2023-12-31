package de.flapdoodle.eval.example.evaluables;

import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluableMap;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.example.Value;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TypedEvaluableMapTest {

	@Test
	void findMatchingEntry() {
		TypedEvaluableMap testee = TypedEvaluableMap.builder()
			.putMap("foo", TypedEvaluables.builder()
				.addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class,
					(valueResolver, evaluationContext, token, argument) -> Value.of(argument.wrapped().add(BigDecimal.ONE))))
				.build())
			.build();

		assertThat(testee.find("foo", 1)).isPresent();
		assertThat(testee.find("foo", 2)).isEmpty();
		assertThat(testee.find("fo", 1)).isEmpty();
	}
}