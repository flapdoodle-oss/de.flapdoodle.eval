package de.flapdoodle.eval.parser;

import de.flapdoodle.checks.Preconditions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Expression2Token {

	public static void parse(String expression) {
		if (false) {
			try {
				new Parser(expression).foo();
			}
			catch (ParseException px) {
				px.printStackTrace();
			}
		}
	}

	static class Parser {
		private final String expression;
		private final char[] chars;
		private int index;
		private final int end;

		private int braceBalance=0;
		private int arrayBalance=0;

		private final List<Token> tokens = new ArrayList<>();

		public Parser(String expression) {
			this.expression = expression;
			this.chars = expression.toCharArray();
			this.index = 0;
			this.end = chars.length;
		}

		protected void foo() throws ParseException {
			System.out.print("parse " + expression);
			nextToken();
			System.out.println(" --> " + index);
		}

		private Optional<Token> previousToken() {
			return tokens.isEmpty()
				? Optional.empty()
				: Optional.of(tokens.get(tokens.size()-1));
		}

		private Token nextToken() throws ParseException {
			skipWS();
			char current = get();
			switch (current) {
				case '"':
					return stringLiteral();
				case '(':
					return braceOpen();
				case ')':
					return braceClose();
				case '[':
					return arrayOpen();
				case ']':
					return arrayClose();
				case '.':

			}

			// TODO muss weg
			return null;
		}

		private Token stringLiteral() throws ParseException {
			int tokenStartIndex = index;
			StringBuilder tokenValue = new StringBuilder();
			boolean inQuote = true;
			while (inQuote) {
				char current = get("Closing quote not found");
				if (current == '\\') {
					skip();
					tokenValue.append(escapeCharacter(current));
				} else if (current == '"') {
					inQuote = false;
				} else {
					tokenValue.append(current);
				}
			}
			return Token.of(tokenStartIndex, tokenValue.toString(), TokenType.STRING_LITERAL);
		}

		private Token braceOpen() {
			Token token = Token.of(index, "(", TokenType.BRACE_OPEN);
			braceBalance++;
			return token;
		}

		private Token braceClose() throws ParseException {
			Token token = Token.of(index, ")", TokenType.BRACE_CLOSE);
			if (braceBalance==0) throw parseException("Unexpected closing brace");
			braceBalance--;
			return token;
		}

		private boolean arrayOpenOrStructureSeparatorNotAllowed() {
			Optional<Token> previousToken = previousToken();

			if (!previousToken.isPresent()) {
				return true;
			}

			switch (previousToken.get().type()) {
				case BRACE_CLOSE:
				case VARIABLE_OR_CONSTANT:
				case ARRAY_CLOSE:
				case STRING_LITERAL:
					return false;
				default:
					return true;
			}
		}

		private boolean arrayCloseAllowed() {
			Optional<Token> previousToken = previousToken();

			if (!previousToken.isPresent()) {
				return false;
			}

			switch (previousToken.get().type()) {
				case BRACE_OPEN:
				case INFIX_OPERATOR:
				case PREFIX_OPERATOR:
				case FUNCTION:
				case COMMA:
				case ARRAY_OPEN:
					return false;
				default:
					return true;
			}
		}


		private Token arrayOpen() throws ParseException {
			Token token = Token.of(index, "[", TokenType.ARRAY_OPEN);
			if (arrayOpenOrStructureSeparatorNotAllowed()) {
				throw parseException("Array open not allowed here");
			}
			arrayBalance++;
			return token;
		}

		private Token arrayClose() throws ParseException {
			Token token = Token.of(index, "]", TokenType.ARRAY_CLOSE);
			if (!arrayCloseAllowed()) {
				throw parseException("Array close not allowed here");
			}
			if (arrayBalance == 0) {
				throw parseException("Unexpected closing array");
			}
			arrayBalance--;
			return token;
		}


		private boolean isNumberStart(int offset) {
			char current = peek(offset);
			if (Character.isDigit(current)) {
				return true;
			}
			return current == '.' && Character.isDigit(peek(offset+1));
		}

		private boolean isNumberChar(int offset) {
			char currentChar = peek(offset);
			char previousChar = peek(offset-1);

			if ((previousChar == 'e' || previousChar == 'E') && currentChar != '.') {
				return Character.isDigit(currentChar) || currentChar == '+' || currentChar == '-';
			}

			if (previousChar == '.') {
				return Character.isDigit(currentChar) || currentChar == 'e' || currentChar == 'E';
			}

			return Character.isDigit(currentChar)
				|| currentChar == '.'
				|| currentChar == 'e'
				|| currentChar == 'E';
		}

		private boolean isCharNumberChar(int offset) {
			if (peek(offset + 1) == 0) {
				return false;
			}
			return isNumberChar(offset+2);
		}

		protected void skip() throws ParseException {
			get();
		}

		protected char peek(int offset) {
			if (index+offset < 0) return 0;
			if (index+offset >= end) return 0;
			return chars[index+offset];
		}

		protected char get() throws ParseException {
			if (index >= end) throw parseException("EOF");
			return chars[index++];
		}

		protected char get(String message) throws ParseException {
			if (index >= end) throw parseException(message);
			return chars[index++];
		}

		protected void skipWS() {
			while (index < end && Character.isWhitespace(chars[index])) {
				index++;
			}
		}

		private char escapeCharacter(int character) throws ParseException {
			switch (character) {
				case '\'':
					return '\'';
				case '"':
					return '"';
				case '\\':
					return '\\';
				case 'n':
					return '\n';
				case 'r':
					return '\r';
				case 't':
					return '\t';
				case 'b':
					return '\b';
				case 'f':
					return '\f';
				default:
					throw parseException("Unknown escape character: \\" + (char) character);
			}
		}

		private ParseException parseException(String message) {
			return new ParseException("'" + expression + "' " + message, index);
		}
	}
}
