package de.flapdoodle.eval.doc;

import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.ExpressionFactory;
import de.flapdoodle.eval.MapBasedVariableResolver;
import de.flapdoodle.eval.VariableResolver;
import de.flapdoodle.eval.evaluables.*;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.ParseException;
import de.flapdoodle.eval.tree.EvalFailedWithException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.testdoc.Recorder;
import de.flapdoodle.testdoc.Recording;
import de.flapdoodle.testdoc.TabSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class HowToTest {
	@RegisterExtension
	public static Recording recording = Recorder.with("HowTo.md", TabSize.spaces(2));

	@Test
	public void example() throws ParseException, EvaluationException {
		recording.begin();
		ExpressionFactory expressionFactory = ExpressionFactory.defaults();
		Expression expression = expressionFactory.parse("a*2");
		Object result = expression.evaluate(VariableResolver.empty()
			.with("a", Value.of(2)));

		assertThat(result).isEqualTo(Value.of(4.0));
		recording.end();
	}

	@Test
	public void usedVariables() throws ParseException, EvaluationException {
		recording.begin();
		ExpressionFactory expressionFactory = ExpressionFactory.defaults();
		Expression expression = expressionFactory.parse("a*2");
		assertThat(expression.usedVariables())
			.containsExactly("a");
		recording.end();
	}

	@Test
	public void minimalCustomSetup() throws ParseException, EvaluationException {
		recording.begin();
		ExpressionFactory expressionFactory = ExpressionFactory.builder()
			.constants(VariableResolver.empty().with("pi", BigDecimal.valueOf(3.1415)))
			.evaluatables(TypedEvaluableMap.builder()
				.putMap("add", TypedEvaluables.builder()
					.addList(TypedEvaluable.of(BigDecimal.class, BigDecimal.class, BigDecimal.class,
						(valueResolver, evaluationContext, token, first, second) -> first.add(second)))
					.build())
				.build())
			.operatorMap(OperatorMap.builder()
				.putInfix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, "add"))
				.build())
			.arrayAccess(TypedEvaluables.builder()
				.addList(TypedEvaluable.of(String.class, String.class, BigDecimal.class,
					(valueResolver, evaluationContext, token, first, second) -> "" + first.charAt(second.intValue())))
				.build())
			.propertyAccess(TypedEvaluables.builder()
				.addList(TypedEvaluable.of(String.class, Map.class, String.class,
					(valueResolver, evaluationContext, token, first, second) -> "" + first.get(second)))
				.build())
			.parseNumber((s, m) -> new BigDecimal(s))
			.stringAsValue(s -> s)
			.exceptionAsParameter(EvalFailedWithException::of)
			.matchException(it -> it instanceof EvalFailedWithException
				? Optional.of(((EvalFailedWithException) it).exception())
				: Optional.empty())
			.build();

		assertThat(expressionFactory.parse("pi").evaluate(VariableResolver.empty()))
			.isEqualTo(BigDecimal.valueOf(3.1415));
		assertThat(expressionFactory.parse("add(2,3)").evaluate(VariableResolver.empty()))
			.isEqualTo(BigDecimal.valueOf(5L));
		assertThat(expressionFactory.parse("2+3").evaluate(VariableResolver.empty()))
			.isEqualTo(BigDecimal.valueOf(5L));
		assertThat(expressionFactory.parse("\"fun\"[1]").evaluate(VariableResolver.empty()))
			.isEqualTo("u");
		MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
		Map<Object, Object> value = new LinkedHashMap<>();
		value.put("key", "stuff");
		assertThat(expressionFactory.parse("map.key")
			.evaluate(mapBasedValueResolver.with("map", value)))
			.isEqualTo("stuff");
		recording.end();
	}
}
