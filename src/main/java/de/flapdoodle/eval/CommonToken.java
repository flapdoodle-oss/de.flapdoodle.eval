package de.flapdoodle.eval;

import de.flapdoodle.eval.parser.TokenType;
import org.immutables.value.Value;

public interface CommonToken {
	int start();

	String value();

	TokenType type();
}
