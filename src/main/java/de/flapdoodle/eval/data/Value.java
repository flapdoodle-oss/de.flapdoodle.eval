/**
 * Copyright (C) 2023
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.eval.data;

import de.flapdoodle.eval.Nullable;
import de.flapdoodle.eval.parser.ASTNode;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Value<T> {
	@org.immutables.value.Value.Parameter
	public abstract T wrapped();

	@org.immutables.value.Value.Derived
	public abstract DataType dataType();

	@org.immutables.value.Value.Immutable
	public static abstract class NullValue extends Value<Void> {

		@Override
		@Nullable
		public abstract Void wrapped();

		@Override
		public DataType dataType() {
			return DataType.NULL;
		}
	}

	public static abstract class ComparableValue<T extends Comparable<T>> extends Value<T> implements Comparable<Value<T>> {
		@org.immutables.value.Value.Auxiliary
		@Override
		public int compareTo(Value<T> other) {
			return wrapped().compareTo(other.wrapped());
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class StringValue extends ComparableValue<String> {
		@Override
		public DataType dataType() {
			return DataType.STRING;
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class NumberValue extends ComparableValue<BigDecimal> {
		@Override
		public DataType dataType() {
			return DataType.NUMBER;
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class BooleanValue extends ComparableValue<Boolean> {
		@Override
		public DataType dataType() {
			return DataType.BOOLEAN;
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class DateTimeValue extends ComparableValue<Instant> {
		@Override
		public DataType dataType() {
			return DataType.DATE_TIME;
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class DurationValue extends ComparableValue<Duration> {
		@Override
		public DataType dataType() {
			return DataType.DURATION;
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class ArrayValue extends Value<ValueArray> {
		@Override
		public DataType dataType() {
			return DataType.ARRAY;
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class StructureValue extends Value<ValueMap> {
		@Override
		public DataType dataType() {
			return DataType.STRUCTURE;
		}
	}

	@org.immutables.value.Value.Immutable
	public static abstract class ExpressionValue extends Value<ASTNode> {
		@Override
		public DataType dataType() {
			return DataType.EXPRESSION;
		}
	}

	public static StringValue of(String value) {
		return ImmutableStringValue.of(value);
	}

	public static NumberValue of(BigDecimal value) {
		return ImmutableNumberValue.of(value);
	}

	public static NumberValue of(double value) {
		// new BigDecimal(Double.toString(value), mathContext); ???
		return Value.of(BigDecimal.valueOf(value));
	}

	private static NullValue NULL=ImmutableNullValue.builder().build();

	public static NullValue ofNull() {
		return NULL;
	}

	public static BooleanValue of(Boolean value) {
		return ImmutableBooleanValue.of(value);
	}

	public static DateTimeValue of(Instant value) {
		return ImmutableDateTimeValue.of(value);
	}

	public static DurationValue of(Duration value) {
		return ImmutableDurationValue.of(value);
	}

	public static ArrayValue of(ValueArray value) {
		return ImmutableArrayValue.of(value);
	}

	public static StructureValue of(ValueMap value) {
		return ImmutableStructureValue.of(value);
	}

	public static <T> StructureValue of(Function<T, Value<?>> mapper, Map<String, T> map) {
		return of(ValueMap.of(map.entrySet().stream()
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> mapper.apply(entry.getValue())))));
	}

	public static ExpressionValue of(ASTNode value) {
		return ImmutableExpressionValue.of(value);
	}

	public static ArrayValue of(Collection<? extends Value<?>> array) {
		return ImmutableArrayValue.of(ValueArray.of(array));
	}

	public static <T> ArrayValue of(Function<T, Value<?>> mapper, Collection<? extends T> array) {
		return of(array.stream().map(mapper).collect(Collectors.toList()));
	}

	public static <T> ArrayValue of(Function<T, Value<?>> mapper, T ... array) {
		return of(Stream.of(array).map(mapper).collect(Collectors.toList()));
	}

	public static ArrayValue of(Value<?> ... array) {
		return of(ValueArray.of(Arrays.asList(array)));
	}
}
