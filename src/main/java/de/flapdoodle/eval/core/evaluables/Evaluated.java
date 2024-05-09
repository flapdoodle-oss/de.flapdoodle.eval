package de.flapdoodle.eval.core.evaluables;

import de.flapdoodle.eval.core.Nullable;
import de.flapdoodle.reflection.TypeInfo;
import org.immutables.value.Value;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class Evaluated<T> {
	@Value.Parameter
	public abstract TypeInfo<T> type();
	@Nullable
	@Value.Parameter
	public abstract T wrapped();

	public static <T> Evaluated<T> value(T value) {
		return ofNullable(TypeInfo.of((Class<T>) value.getClass()), value);
	}

	public static <T> Evaluated<T> ofNullable(Class<T> type, @Nullable T value) {
		return ofNullable(TypeInfo.of(type), value);
	}

	public static <T> Evaluated<T> ofNullable(TypeInfo<T> type, @Nullable T value) {
		return ImmutableEvaluated.of(type, value);
	}

	public static List<?> unwrap(List<? extends Evaluated<?>> src) {
		return src.stream().map(it -> it.wrapped()).collect(Collectors.toList());
	}

	public static <T> List<? extends Evaluated<T>> wrap(List<T> src) {
		return src.stream().map(it -> Evaluated.value(it)).collect(Collectors.toList());
	}

	public static <T> List<? extends Evaluated<T>> asList(T ... src) {
		return wrap(Arrays.asList(src));
	}
}
