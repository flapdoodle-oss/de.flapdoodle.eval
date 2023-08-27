package de.flapdoodle.eval.operators.booleans;

import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.config.Configuration;
import de.flapdoodle.eval.config.MapBasedOperatorResolver;
import de.flapdoodle.eval.config.OperatorResolver;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SameTest {

	@Test
	void testInfixEqualsVariables() throws EvaluationException, ParseException {
		Expression expression = Expression.of("a===b", Configuration.defaultConfiguration()
			.withOperatorResolver(MapBasedOperatorResolver.builder()
				.putInfixOperators("===", new Same())
				.build()));

		BigDecimal value = new BigDecimal("1.4");

		assertThat(expression.evaluate(ValueResolver.empty()
				.with("a", value)
				.with("b", value))
			.wrapped())
			.isEqualTo(Boolean.TRUE);

		assertThat(expression.evaluate(ValueResolver.empty()
				.with("a", "Hello")
				.with("b", "Hello"))
			.wrapped())
			.isEqualTo(Boolean.TRUE);

		assertThat(expression.evaluate(ValueResolver.empty()
				.with("a", "Hello")
				.with("b", "Goodbye"))
			.wrapped())
			.isEqualTo(Boolean.FALSE);

		assertThat(expression.evaluate(ValueResolver.empty()
				.with("a", true)
				.with("b", true))
			.wrapped())
			.isEqualTo(Boolean.TRUE);

		assertThat(expression.evaluate(ValueResolver.empty()
				.with("a", false)
				.with("b", true))
			.wrapped())
			.isEqualTo(Boolean.FALSE);
	}

}