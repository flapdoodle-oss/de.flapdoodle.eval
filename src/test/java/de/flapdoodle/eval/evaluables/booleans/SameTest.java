package de.flapdoodle.eval.evaluables.booleans;

import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.ExpressionFactory;
import de.flapdoodle.eval.parser.ParseException;
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

		assertThat(expression.evaluate(ValueResolver.empty()
				.with("a", value)
				.with("b", value))
			)
			.isEqualTo(Value.TRUE);

		assertThat(expression.evaluate(ValueResolver.empty()
				.with("a", "Hello")
				.with("b", "Hello"))
			)
			.isEqualTo(Value.TRUE);

		assertThat(expression.evaluate(ValueResolver.empty()
				.with("a", "Hello")
				.with("b", "Goodbye"))
			)
			.isEqualTo(Value.FALSE);

		assertThat(expression.evaluate(ValueResolver.empty()
				.with("a", true)
				.with("b", true))
			)
			.isEqualTo(Value.TRUE);

		assertThat(expression.evaluate(ValueResolver.empty()
				.with("a", false)
				.with("b", true))
			)
			.isEqualTo(Value.FALSE);
	}

}