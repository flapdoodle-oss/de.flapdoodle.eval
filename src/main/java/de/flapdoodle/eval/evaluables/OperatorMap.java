package de.flapdoodle.eval.evaluables;

import org.immutables.value.Value;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class OperatorMap implements HasOperator {
	protected abstract Map<String, OperatorMapping> prefix();
	protected abstract Map<String, OperatorMapping> infix();
	protected abstract Map<String, OperatorMapping> postfix();

	@Value.Auxiliary
	public Optional<OperatorMapping> prefixOperator(String name) {
		return Optional.ofNullable(prefix().get(name));
	}

	@Value.Auxiliary
	public Optional<OperatorMapping> infixOperator(String name) {
		return Optional.ofNullable(infix().get(name));
	}

	@Value.Auxiliary
	public Optional<OperatorMapping> postfixOperator(String name) {
		return Optional.ofNullable(postfix().get(name));
	}

	public static ImmutableOperatorMap.Builder builder() {
		return ImmutableOperatorMap.builder();
	}

	public OperatorMap andThen(OperatorMap fallback) {
		return builder().from(this)
				.putAllInfix(onlyMissing(fallback.infix(), infix().keySet()))
				.putAllPrefix(onlyMissing(fallback.prefix(), prefix().keySet()))
				.putAllPostfix(onlyMissing(fallback.postfix(), postfix().keySet()))
				.build();
	}

	private static <K, V> Map<K, V> onlyMissing(Map<K, V> src, Set<K> exclude) {
		return src.entrySet().stream()
				.filter(e -> !exclude.contains(e.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@Override
	public boolean hasStartingWith(OperatorType type, String value) {
		Set<String> keys=Collections.emptySet();

		switch (type) {
			case Infix: keys = infix().keySet(); break;
			case Prefix: keys = prefix().keySet(); break;
			case Postfix: keys = postfix().keySet(); break;
		}
		
		return keys.stream().anyMatch(it -> it.startsWith(value));
	}

	@Override
	public boolean matching(OperatorType type, String value) {
		switch (type) {
			case Infix: return infixOperator(value).isPresent();
			case Prefix: return prefixOperator(value).isPresent();
			case Postfix: return postfixOperator(value).isPresent();
		}
		return false;
	}
}
