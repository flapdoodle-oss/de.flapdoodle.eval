package de.flapdoodle.eval;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.evaluate.Node;
import de.flapdoodle.eval.parser.ParseException;

import java.math.MathContext;
import java.time.ZoneId;

@org.immutables.value.Value.Immutable
public abstract class ParsedExpression {
	protected abstract MathContext mathContext();
	protected abstract ZoneId zoneId();
	protected abstract Node root();

	public Value<?> evaluate(ValueResolver variableResolver) throws EvaluationException, ParseException {
		return root().evaluate(variableResolver, EvaluationContext.builder()
			.mathContext(mathContext())
			.zoneId(zoneId())
			.build());
	}

	public static ImmutableParsedExpression.Builder builder() {
		return ImmutableParsedExpression.builder();
	}
}
