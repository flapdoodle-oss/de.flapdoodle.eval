package de.flapdoodle.eval.parser;

public enum OperatorType {
	INFIX(TokenType.INFIX_OPERATOR),
	PREFIX(TokenType.PREFIX_OPERATOR),
	POSTFIX(TokenType.POSTFIX_OPERATOR);

	private final TokenType tokenType;

	OperatorType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	public TokenType tokenType() {
		return tokenType;
	}
}
