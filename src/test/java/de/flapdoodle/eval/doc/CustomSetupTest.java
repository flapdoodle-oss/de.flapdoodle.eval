package de.flapdoodle.eval.doc;

import de.flapdoodle.eval.core.ExpressionFactory;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.OperatorMap;
import de.flapdoodle.eval.core.evaluables.OperatorMapping;
import de.flapdoodle.eval.core.evaluables.Precedence;
import de.flapdoodle.eval.core.evaluables.TypedEvaluableMap;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.exceptions.ParseException;
import de.flapdoodle.eval.core.tree.EvalFailedWithException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomSetupTest {

	@Test
	public void example() throws ParseException, EvaluationException {
		ExpressionFactory expressionFactory = ExpressionFactory.builder()
			.constants(VariableResolver.empty())
			.evaluatables(TypedEvaluableMap.builder()
				.putMap("add", new Plus())
				.putMap("minus", new Minus())
				.build())
			.operatorMap(OperatorMap.builder()
				.putPrefix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "add"))
				.putPrefix("-", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "minus"))

				.putInfix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "add"))
				.putInfix("-", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "minus"))
				.build())
			.arrayAccess(new ArrayAccess())
			.propertyAccess(new PropertyAccess())
			.numberAsValue((s, m) -> Integer.parseInt(s))
			.stringAsValue(s -> s)
			.exceptionMapper(EvalFailedWithException.mapper())
			.build();

		assertThat(expressionFactory.parse("add(2,3)").evaluate(VariableResolver.empty()))
			.isEqualTo(5);
		assertThat(expressionFactory.parse("2+3").evaluate(VariableResolver.empty()))
			.isEqualTo(5);
		assertThat(expressionFactory.parse("2-3").evaluate(VariableResolver.empty()))
			.isEqualTo(-1);
		assertThat(expressionFactory.parse("-2").evaluate(VariableResolver.empty()))
			.isEqualTo(-2);
	}
}
