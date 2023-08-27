package de.flapdoodle.eval;

import de.flapdoodle.eval.parser.TokenType;

public interface CommonToken {
	int start();

	String value();

	TokenType type();
}
