package de.flapdoodle.eval;

import de.flapdoodle.eval.parser.TokenType;
import org.immutables.value.Value;

@Value.Immutable
public interface CommonToken {
	@Value.Parameter
	int start();

	@Value.Parameter
	String value();

	@Value.Parameter
	TokenType type();

	static CommonToken of(int startPosition, String value, TokenType type) {
		return ImmutableCommonToken.of(startPosition, value, type);
	}
}
