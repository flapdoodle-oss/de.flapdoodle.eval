package de.flapdoodle.eval.core.evaluables;

import org.immutables.value.Value;

@Value.Immutable
public interface OperatorMapping {
	/**
	 * @return The operator's precedence.
	 */
	int precedence();

	/**
	 * If operators with same precedence are evaluated from left to right.
	 *
	 * @return The associativity.
	 */
	boolean isLeftAssociative();

	/**
	 * name of evaluatable delegate
	 */
	String evaluatable();

	static ImmutableOperatorMapping.Builder builder() {
		return ImmutableOperatorMapping.builder();
	}

	static OperatorMapping of(int precedence, boolean isLeftAssociative, String evaluatable) {
		return builder()
				.precedence(precedence)
				.isLeftAssociative(isLeftAssociative)
				.evaluatable(evaluatable)
				.build();
	}

	static OperatorMapping of(Precedence precedence, boolean isLeftAssociative, String evaluatable) {
		return of(precedence.value(), isLeftAssociative, evaluatable);
	}

	static OperatorMapping of(Precedence precedence, String evaluatable) {
		return of(precedence, true, evaluatable);
	}
}
