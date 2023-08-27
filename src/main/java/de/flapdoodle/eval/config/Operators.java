package de.flapdoodle.eval.config;

import de.flapdoodle.checks.Preconditions;
import de.flapdoodle.eval.parser.OperatorType;
import de.flapdoodle.types.Pair;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class Operators {
	public abstract List<Pair<String, OperatorType>> map();

	@Value.Lazy
	protected Map<OperatorType, Set<String>> groupedByType() {
		return map().stream()
			.collect(Collectors.groupingBy(Pair::second, Collectors.mapping(Pair::first, Collectors.toSet())));
	}

	@Value.Auxiliary
	public boolean hasStartingWith(OperatorType type, String value) {
		Set<String> labelSet = groupedByType().get(type);
		return labelSet != null && labelSet.stream().anyMatch(it -> it.startsWith(value));
	}

	@Value.Auxiliary
	public boolean matching(OperatorType type, String value) {
		Set<String> set = groupedByType().get(type);
		return set != null && set.contains(value);
	}

	@Value.Auxiliary
	public Operators andThen(Operators operators) {
		return ImmutableOperators.builder()
			.from(this)
			.addAllMap(operators.map())
			.build();
	}


	public static ImmutableOperators.Builder builder() {
		return ImmutableOperators.builder();
	}
}
