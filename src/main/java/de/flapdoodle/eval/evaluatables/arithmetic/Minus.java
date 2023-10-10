package de.flapdoodle.eval.evaluatables.arithmetic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.time.Duration;

public class Minus extends TypedEvaluatables.Wrapper {

	public static class Number implements TypedEvaluatable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first,
			Value.NumberValue second) throws EvaluationException {
			return Value.of(first.wrapped().subtract(second.wrapped(), evaluationContext.mathContext()));
		}
	}

	public static class DateTimeDuration implements TypedEvaluatable.Arg2<Value.DateTimeValue, Value.DurationValue, Value.DateTimeValue> {

		@Override
		public Value.DateTimeValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue first,
			Value.DurationValue second) throws EvaluationException {
			return Value.of(first.wrapped().minus(second.wrapped()));
		}
	}

	public static class DateTimeDateTime implements TypedEvaluatable.Arg2<Value.DateTimeValue, Value.DateTimeValue, Value.DurationValue> {

		@Override
		public Value.DurationValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue first,
			Value.DateTimeValue second) throws EvaluationException {
			return Value.of(Duration.ofMillis(first.wrapped().toEpochMilli() - second.wrapped().toEpochMilli()));
		}
	}

	public static class Durations implements TypedEvaluatable.Arg2<Value.DurationValue, Value.DurationValue, Value.DurationValue> {

		@Override
		public Value.DurationValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.DurationValue first,
			Value.DurationValue second) throws EvaluationException {
			return Value.of(first.wrapped().minus(second.wrapped()));
		}
	}

	public static class DateTimeNumber implements TypedEvaluatable.Arg2<Value.DateTimeValue, Value.NumberValue, Value.DateTimeValue> {

		@Override
		public Value.DateTimeValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue first,
			Value.NumberValue second) throws EvaluationException {
			return Value.of(first.wrapped().minus(Duration.ofMillis(second.wrapped().longValue())));
		}
	}

	public static class Negate implements TypedEvaluatable.Arg1<Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first) throws EvaluationException {
			return Value.of(first.wrapped().negate(evaluationContext.mathContext()));
		}
	}

	public Minus() {
		super(TypedEvaluatables.builder()
			.addList(TypedEvaluatable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class, new Number()))
			.addList(TypedEvaluatable.of(Value.DateTimeValue.class, Value.DateTimeValue.class, Value.DurationValue.class, new DateTimeDuration()))
			.addList(TypedEvaluatable.of(Value.DurationValue.class, Value.DateTimeValue.class, Value.DateTimeValue.class, new DateTimeDateTime()))
			.addList(TypedEvaluatable.of(Value.DurationValue.class, Value.DurationValue.class, Value.DurationValue.class, new Durations()))
			.addList(TypedEvaluatable.of(Value.DateTimeValue.class, Value.DateTimeValue.class, Value.NumberValue.class, new DateTimeNumber()))
			.addList(TypedEvaluatable.of(Value.NumberValue.class, Value.NumberValue.class, new Negate()))
			.build());
	}
}
