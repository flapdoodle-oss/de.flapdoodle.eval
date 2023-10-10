package de.flapdoodle.eval.evaluatables.arithmetic;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.evaluatables.TypedEvaluatable;
import de.flapdoodle.eval.evaluatables.TypedEvaluatables;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

public class Plus extends TypedEvaluatables.Wrapper {

	public static class Number implements TypedEvaluatable.Arg2<Value.NumberValue, Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first,
			Value.NumberValue second) throws EvaluationException {
			return Value.of(first.wrapped().add(second.wrapped(), evaluationContext.mathContext()));
		}
	}

	public static class DateTimeDuration implements TypedEvaluatable.Arg2<Value.DateTimeValue, Value.DurationValue, Value.DateTimeValue> {

		@Override
		public Value.DateTimeValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue first,
			Value.DurationValue second) throws EvaluationException {
			return Value.of(first.wrapped().plus(second.wrapped()));
		}
	}

	public static class Durations implements TypedEvaluatable.Arg2<Value.DurationValue, Value.DurationValue, Value.DurationValue> {

		@Override
		public Value.DurationValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.DurationValue first,
			Value.DurationValue second) throws EvaluationException {
			return Value.of(first.wrapped().plus(second.wrapped()));
		}
	}

	public static class DateTimeNumber implements TypedEvaluatable.Arg2<Value.DateTimeValue, Value.NumberValue, Value.DateTimeValue> {

		@Override
		public Value.DateTimeValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.DateTimeValue first,
			Value.NumberValue second) throws EvaluationException {
			return Value.of(first.wrapped().plus(Duration.ofMillis(second.wrapped().longValue())));
		}
	}

	public static class ToString<A extends Value<?>, B extends Value<?>> implements TypedEvaluatable.Arg2<A, B, Value.StringValue> {

		@Override
		public Value.StringValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, A first,
			B second) throws EvaluationException {
			return Value.of(first.wrapped().toString() + second.wrapped());
		}
	}

//	public static class Prefix implements TypedEvaluatable.Arg1<Value.NumberValue, Value.NumberValue> {
//
//		@Override
//		public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, Value.NumberValue first) throws EvaluationException {
//			return Value.of(first.wrapped().plus(evaluationContext.mathContext()));
//		}
//	}

	public static class Sum implements TypedEvaluatable.VarArg1<Value.NumberValue, Value.NumberValue> {

		@Override
		public Value.NumberValue evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, List<Value.NumberValue> arguments)
			throws EvaluationException {
			BigDecimal sum = BigDecimal.ZERO;
			for (Value.NumberValue parameter : arguments) {
				sum = sum.add(parameter.wrapped(), evaluationContext.mathContext());
			}
			return Value.of(sum);
		}
	}


	public Plus() {
		super(TypedEvaluatables.builder()
			.addList(TypedEvaluatable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class, new Number()))
			.addList(TypedEvaluatable.of(Value.DateTimeValue.class, Value.DateTimeValue.class, Value.DurationValue.class, new DateTimeDuration()))
			.addList(TypedEvaluatable.of(Value.DurationValue.class, Value.DurationValue.class, Value.DurationValue.class, new Durations()))
			.addList(TypedEvaluatable.of(Value.DateTimeValue.class, Value.DateTimeValue.class, Value.NumberValue.class, new DateTimeNumber()))
			.addList(TypedEvaluatable.of(Value.StringValue.class, Value.class, Value.class, new ToString<>()))
//			.addList(TypedEvaluatable.of(Value.NumberValue.class, Value.NumberValue.class, new Prefix()))
			.addList(TypedEvaluatable.ofVarArg(Value.NumberValue.class, Value.NumberValue.class, new Sum()))
			.build());
	}
}
