package de.flapdoodle.eval.evaluables.arithmetic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.VariableResolver;
import de.flapdoodle.eval.evaluables.TypedEvaluable;
import de.flapdoodle.eval.evaluables.TypedEvaluables;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;

import java.time.Duration;

public class Minus extends TypedEvaluables.Wrapper {

	public static class Number implements TypedEvaluable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first,
										  Value.NumberValue second) throws EvaluationException {
			return Value.of(first.wrapped().subtract(second.wrapped(), evaluationContext.mathContext()));
		}
	}

	public static class DateTimeDuration implements TypedEvaluable.Arg2<Value.DateTimeValue, Value.DurationValue, Value.DateTimeValue> {

		@Override
		public Value.DateTimeValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue first,
											Value.DurationValue second) throws EvaluationException {
			return Value.of(first.wrapped().minus(second.wrapped()));
		}
	}

	public static class DateTimeDateTime implements TypedEvaluable.Arg2<Value.DateTimeValue, Value.DateTimeValue, Value.DurationValue> {

		@Override
		public Value.DurationValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue first,
											Value.DateTimeValue second) throws EvaluationException {
			return Value.of(Duration.ofMillis(first.wrapped().toEpochMilli() - second.wrapped().toEpochMilli()));
		}
	}

	public static class Durations implements TypedEvaluable.Arg2<Value.DurationValue, Value.DurationValue, Value.DurationValue> {

		@Override
		public Value.DurationValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.DurationValue first,
											Value.DurationValue second) throws EvaluationException {
			return Value.of(first.wrapped().minus(second.wrapped()));
		}
	}

	public static class DateTimeNumber implements TypedEvaluable.Arg2<Value.DateTimeValue, Value.NumberValue, Value.DateTimeValue> {

		@Override
		public Value.DateTimeValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue first,
											Value.NumberValue second) throws EvaluationException {
			return Value.of(first.wrapped().minus(Duration.ofMillis(second.wrapped().longValue())));
		}
	}

	public static class Negate implements TypedEvaluable.Arg1<Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first) throws EvaluationException {
			return Value.of(first.wrapped().negate(evaluationContext.mathContext()));
		}
	}

	public Minus() {
		super(TypedEvaluables.builder()
			.addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class, new Number()))
			.addList(TypedEvaluable.of(Value.DateTimeValue.class, Value.DateTimeValue.class, Value.DurationValue.class, new DateTimeDuration()))
			.addList(TypedEvaluable.of(Value.DurationValue.class, Value.DateTimeValue.class, Value.DateTimeValue.class, new DateTimeDateTime()))
			.addList(TypedEvaluable.of(Value.DurationValue.class, Value.DurationValue.class, Value.DurationValue.class, new Durations()))
			.addList(TypedEvaluable.of(Value.DateTimeValue.class, Value.DateTimeValue.class, Value.NumberValue.class, new DateTimeNumber()))
			.addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, new Negate()))
			.build());
	}
}
