package de.flapdoodle.eval.doc;

import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.ExpressionFactory;
import de.flapdoodle.eval.evaluables.*;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.ParseException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueMap;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.testdoc.Recorder;
import de.flapdoodle.testdoc.Recording;
import de.flapdoodle.testdoc.TabSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

public class HowToTest {
	@RegisterExtension
	public static Recording recording = Recorder.with("HowTo.md", TabSize.spaces(2));

	@Test
	public void example() throws ParseException, EvaluationException {
		recording.begin();
		ExpressionFactory expressionFactory = ExpressionFactory.defaults();
		Expression expression = expressionFactory.parse("a*2");
		Object result = expression.evaluate(ValueResolver.empty()
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
			.constants(ValueResolver.empty().with("pi", Value.of(3.1415)))
			.evaluatables(TypedEvaluableMap.builder()
				.putMap("add", TypedEvaluables.builder()
					.addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class,
						(valueResolver, evaluationContext, token, first, second) -> Value.of(first.wrapped().add(second.wrapped()))))
					.build())
				.build())
			.operatorMap(OperatorMap.builder()
				.putInfix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, "add"))
				.build())
			.arrayAccess(TypedEvaluables.builder()
				.addList(TypedEvaluable.of(Value.StringValue.class, Value.StringValue.class, Value.NumberValue.class,
					(valueResolver, evaluationContext, token, first, second) -> Value.of("" + first.wrapped().charAt(second.wrapped().intValue()))))
				.build())
			.propertyAccess(TypedEvaluables.builder()
				.addList(TypedEvaluable.of(Value.StringValue.class, Value.MapValue.class, Value.StringValue.class,
					(valueResolver, evaluationContext, token, first, second) -> Value.of("" + first.wrapped().get(second.wrapped()).wrapped())))
				.build())
			.build();

		assertThat(expressionFactory.parse("pi").evaluate(ValueResolver.empty()))
			.isEqualTo(Value.of(3.1415));
		assertThat(expressionFactory.parse("add(2,3)").evaluate(ValueResolver.empty()))
			.isEqualTo(Value.of(BigDecimal.valueOf(5L)));
		assertThat(expressionFactory.parse("2+3").evaluate(ValueResolver.empty()))
			.isEqualTo(Value.of(BigDecimal.valueOf(5L)));
		assertThat(expressionFactory.parse("\"fun\"[1]").evaluate(ValueResolver.empty()))
			.isEqualTo(Value.of("u"));
		assertThat(expressionFactory.parse("map.key")
			.evaluate(ValueResolver.empty().with("map", ValueMap.builder()
				.putValues("key", Value.of("stuff"))
				.build())))
			.isEqualTo(Value.of("stuff"));
		recording.end();
	}
}
