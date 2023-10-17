package de.flapdoodle.eval.evaluables.booleans;

import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.ExpressionFactory;
import de.flapdoodle.eval.parser.ParseException;
import de.flapdoodle.eval.values.MapBasedValueResolver;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SameTest {

	@Test
	void testInfixEqualsVariables() throws EvaluationException, ParseException {
		Expression expression = ExpressionFactory.defaults()
				.parse("a===b");

		BigDecimal value = new BigDecimal("1.4");

		MapBasedValueResolver mapBasedValueResolver8 = ValueResolver.empty();
		MapBasedValueResolver mapBasedValueResolver9 = mapBasedValueResolver8.with("a", Value.of(value));
		assertThat(expression.evaluate(mapBasedValueResolver9.with("b", Value.of(value)))
			)
			.isEqualTo(Value.TRUE);

		MapBasedValueResolver mapBasedValueResolver6 = ValueResolver.empty();
		MapBasedValueResolver mapBasedValueResolver7 = mapBasedValueResolver6.with("a", Value.of("Hello"));
		assertThat(expression.evaluate(mapBasedValueResolver7.with("b", Value.of("Hello")))
			)
			.isEqualTo(Value.TRUE);

		MapBasedValueResolver mapBasedValueResolver4 = ValueResolver.empty();
		MapBasedValueResolver mapBasedValueResolver5 = mapBasedValueResolver4.with("a", Value.of("Hello"));
		assertThat(expression.evaluate(mapBasedValueResolver5.with("b", Value.of("Goodbye")))
			)
			.isEqualTo(Value.FALSE);

        MapBasedValueResolver mapBasedValueResolver2 = ValueResolver.empty();
        MapBasedValueResolver mapBasedValueResolver3 = mapBasedValueResolver2.with("a", Value.of(true));
        assertThat(expression.evaluate(mapBasedValueResolver3.with("b", Value.of(true)))
			)
			.isEqualTo(Value.TRUE);

        MapBasedValueResolver mapBasedValueResolver = ValueResolver.empty();
        MapBasedValueResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Value.of(false));
        assertThat(expression.evaluate(mapBasedValueResolver1.with("b", Value.of(true)))
			)
			.isEqualTo(Value.FALSE);
	}

}