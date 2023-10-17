package de.flapdoodle.eval.evaluables.booleans;

import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.ExpressionFactory;
import de.flapdoodle.eval.MapBasedVariableResolver;
import de.flapdoodle.eval.VariableResolver;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.ParseException;
import de.flapdoodle.eval.values.Value;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SameTest {

	@Test
	void testInfixEqualsVariables() throws EvaluationException, ParseException {
		Expression expression = ExpressionFactory.defaults()
				.parse("a===b");

		BigDecimal value = new BigDecimal("1.4");

		MapBasedVariableResolver mapBasedValueResolver8 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver9 = mapBasedValueResolver8.with("a", Value.of(value));
		assertThat(expression.evaluate(mapBasedValueResolver9.with("b", Value.of(value)))
			)
			.isEqualTo(Value.TRUE);

		MapBasedVariableResolver mapBasedValueResolver6 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver7 = mapBasedValueResolver6.with("a", Value.of("Hello"));
		assertThat(expression.evaluate(mapBasedValueResolver7.with("b", Value.of("Hello")))
			)
			.isEqualTo(Value.TRUE);

		MapBasedVariableResolver mapBasedValueResolver4 = VariableResolver.empty();
		MapBasedVariableResolver mapBasedValueResolver5 = mapBasedValueResolver4.with("a", Value.of("Hello"));
		assertThat(expression.evaluate(mapBasedValueResolver5.with("b", Value.of("Goodbye")))
			)
			.isEqualTo(Value.FALSE);

        MapBasedVariableResolver mapBasedValueResolver2 = VariableResolver.empty();
        MapBasedVariableResolver mapBasedValueResolver3 = mapBasedValueResolver2.with("a", Value.of(true));
        assertThat(expression.evaluate(mapBasedValueResolver3.with("b", Value.of(true)))
			)
			.isEqualTo(Value.TRUE);

        MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
        MapBasedVariableResolver mapBasedValueResolver1 = mapBasedValueResolver.with("a", Value.of(false));
        assertThat(expression.evaluate(mapBasedValueResolver1.with("b", Value.of(true)))
			)
			.isEqualTo(Value.FALSE);
	}

}