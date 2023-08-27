/**
 * Copyright (C) 2023
 * Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.eval.parser;

import de.flapdoodle.eval.config.Configuration;
import de.flapdoodle.eval.config.OperatorResolver;
import de.flapdoodle.eval.nparser.Token;
import de.flapdoodle.eval.operators.InfixOperator;
import de.flapdoodle.eval.operators.PostfixOperator;
import de.flapdoodle.eval.operators.PrefixOperator;
import de.flapdoodle.eval.parser.TokenType;
import de.flapdoodle.eval.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The tokenizer is responsible to parse a string and return a list of tokens. The order of tokens
 * will follow the infix expression notation, skipping any blank characters.
 */
public class Tokenizer {
	private final String expressionString;
	private final char[] chars;
	private final int end;

	private final OperatorResolver operatorDictionary;

	private final Configuration configuration;

	private final List<de.flapdoodle.eval.nparser.Token> tokens = new ArrayList<>();

	private int index = 0;

	private int braceBalance;

	private int arrayBalance;

	public Tokenizer(String expressionString, Configuration configuration) {
		this.expressionString = expressionString;
		this.chars = expressionString.toCharArray();
		this.end = chars.length;
		
		this.configuration = configuration;
		this.operatorDictionary = configuration.getOperatorResolver();
	}

	/**
	 * Parse the given expression and return a list of tokens, representing the expression.
	 *
	 * @return A list of expression tokens.
	 * @throws ParseException When the expression can't be parsed.
	 */
	public List<de.flapdoodle.eval.nparser.Token> parse() throws ParseException {

		Optional<de.flapdoodle.eval.nparser.Token> token;
		while ((token = nextToken()).isPresent()) {
			de.flapdoodle.eval.nparser.Token currentToken = token.get();
			if (implicitMultiplicationPossible(currentToken)) {
				if (configuration.isImplicitMultiplicationAllowed()) {
					de.flapdoodle.eval.nparser.Token multiplication =
						de.flapdoodle.eval.nparser.Token.of(
							currentToken.start(),
							"*",
							TokenType.INFIX_OPERATOR);
					tokens.add(multiplication);
				} else {
					throw new ParseException(currentToken, "Missing operator");
				}
			}
			validateToken(currentToken);
			tokens.add(currentToken);
		}

		if (braceBalance > 0) {
			throw new ParseException(expressionString, "Closing brace not found");
		}

		if (arrayBalance > 0) {
			throw new ParseException(expressionString, "Closing array not found");
		}

		return tokens;
	}

	private boolean implicitMultiplicationPossible(de.flapdoodle.eval.nparser.Token currentToken) {
		switch (currentToken.type()) {
			case BRACE_OPEN:
				return isPreviousTokenType(TokenType.BRACE_CLOSE, TokenType.NUMBER_LITERAL);
			case VARIABLE_OR_CONSTANT:
				return isPreviousTokenType(TokenType.NUMBER_LITERAL);
			default:
				return false;
		}
	}

	private void validateToken(de.flapdoodle.eval.nparser.Token currentToken) throws ParseException {
		if (isPreviousTokenType(TokenType.INFIX_OPERATOR) && invalidTokenAfterInfixOperator(currentToken)) {
			throw new ParseException(currentToken, "Unexpected token after infix operator");
		}
	}

	private boolean invalidTokenAfterInfixOperator(de.flapdoodle.eval.nparser.Token token) {
		switch (token.type()) {
			case INFIX_OPERATOR:
			case BRACE_CLOSE:
			case COMMA:
				return true;
			default:
				return false;
		}
	}

	private Optional<de.flapdoodle.eval.nparser.Token> nextToken() throws ParseException {
		// blanks are always skipped.
		skipBlanks();

		return eof()
			? Optional.empty()
			: Optional.of(parseNextToken());
	}
	
	private de.flapdoodle.eval.nparser.Token parseNextToken() throws ParseException {
		char currentChar=get();
		// we have a token start, identify and parse it
		if (currentChar == '"') {
			return parseStringLiteral();
		} else if (currentChar == '(') {
			return parseBraceOpen();
		} else if (currentChar == ')') {
			return parseBraceClose();
		} else if (currentChar == '[' && configuration.isArraysAllowed()) {
			return parseArrayOpen();
		} else if (currentChar == ']' && configuration.isArraysAllowed()) {
			return parseArrayClose();
		} else if (currentChar == '.'
			&& !isNextCharNumberChar()
			&& configuration.isStructuresAllowed()) {
			return parseStructureSeparator();
		} else if (currentChar == ',') {
			de.flapdoodle.eval.nparser.Token token = de.flapdoodle.eval.nparser.Token.of(index, ",", TokenType.COMMA);
			next();
			return token;
		} else if (isIdentifierStart(currentChar)) {
			return parseIdentifier();
		} else if (isNumberStart(0)) {
			return parseNumberLiteral();
		} else {
			return parseOperator();
		}
	}

	private de.flapdoodle.eval.nparser.Token parseStructureSeparator() throws ParseException {
		de.flapdoodle.eval.nparser.Token token = de.flapdoodle.eval.nparser.Token.of(index, ".", TokenType.STRUCTURE_SEPARATOR);
		if (arrayOpenOrStructureSeparatorNotAllowed()) {
			throw new ParseException(token, "Structure separator not allowed here");
		}
		next();
		return token;
	}

	private de.flapdoodle.eval.nparser.Token parseArrayClose() throws ParseException {
		de.flapdoodle.eval.nparser.Token token = de.flapdoodle.eval.nparser.Token.of(index, "]", TokenType.ARRAY_CLOSE);
		if (!arrayCloseAllowed()) {
			throw new ParseException(token, "Array close not allowed here");
		}
		next();
		if (arrayBalance <= 0) {
			throw new ParseException(token, "Unexpected closing array");
		}
		arrayBalance--;
		return token;
	}

	private de.flapdoodle.eval.nparser.Token parseArrayOpen() throws ParseException {
		de.flapdoodle.eval.nparser.Token token = de.flapdoodle.eval.nparser.Token.of(index, "[", TokenType.ARRAY_OPEN);
		if (arrayOpenOrStructureSeparatorNotAllowed()) {
			throw new ParseException(token, "Array open not allowed here");
		}
		next();
		arrayBalance++;
		return token;
	}

	private de.flapdoodle.eval.nparser.Token parseBraceClose() throws ParseException {
		de.flapdoodle.eval.nparser.Token token = de.flapdoodle.eval.nparser.Token.of(index, ")", TokenType.BRACE_CLOSE);
		next();
		if (braceBalance <= 0) {
			throw new ParseException(token, "Unexpected closing brace");
		}
		braceBalance--;
		return token;
	}

	private de.flapdoodle.eval.nparser.Token parseBraceOpen() {
		de.flapdoodle.eval.nparser.Token token = de.flapdoodle.eval.nparser.Token.of(index, "(", TokenType.BRACE_OPEN);
		next();
		braceBalance++;
		return token;
	}

	private boolean isPreviousTokenType(TokenType ... match) {
		return matchPreviousTokenType(match).orElse(false);
	}

	private Optional<Boolean> dontMatchPreviousTokenType(TokenType... match) {
		return matchPreviousTokenType(match).map(it -> !it);
	}

	private Optional<Boolean> matchPreviousTokenType(TokenType... match) {
		return (tokens.isEmpty()
			? Optional.empty()
			: Optional.of(tokens.get(tokens.size() - 1).type()))
			.map(type -> {
				for (TokenType m : match) {
					if (m == type) return true;
				}
				return false;
			});
	}

	private de.flapdoodle.eval.nparser.Token parseOperator() throws ParseException {
		int tokenStartIndex = index;
		StringBuilder tokenValue = new StringBuilder();
		while (true) {
			char currentChar=get();
			tokenValue.append(currentChar);
			String tokenString = tokenValue.toString();
			String possibleNextOperator = tokenString + peek(1); // (char) peekNextChar();
			boolean possibleNextOperatorFound =
				(prefixOperatorAllowed() && operatorDictionary.hasOperator(PrefixOperator.class, possibleNextOperator))
					|| (postfixOperatorAllowed()
					&& operatorDictionary.hasOperator(PostfixOperator.class, possibleNextOperator))
					|| (infixOperatorAllowed()
					&& operatorDictionary.hasOperator(InfixOperator.class, possibleNextOperator));
			next();
			if (!possibleNextOperatorFound) {
				break;
			}
		}
		String tokenString = tokenValue.toString();
		if (prefixOperatorAllowed() && operatorDictionary.hasOperator(PrefixOperator.class, tokenString)) {
			return de.flapdoodle.eval.nparser.Token.of(tokenStartIndex, tokenString, TokenType.PREFIX_OPERATOR);
		} else if (postfixOperatorAllowed() && operatorDictionary.hasOperator(PostfixOperator.class, tokenString)) {
			return de.flapdoodle.eval.nparser.Token.of(tokenStartIndex, tokenString, TokenType.POSTFIX_OPERATOR);
		} else if (operatorDictionary.hasOperator(InfixOperator.class, tokenString)) {
			return de.flapdoodle.eval.nparser.Token.of(tokenStartIndex, tokenString, TokenType.INFIX_OPERATOR);
		} else if (tokenString.equals(".") && configuration.isStructuresAllowed()) {
			return de.flapdoodle.eval.nparser.Token.of(tokenStartIndex, tokenString, TokenType.STRUCTURE_SEPARATOR);
		}
		throw new ParseException(
			tokenStartIndex,
			tokenStartIndex + tokenString.length() - 1,
			tokenString,
			"Undefined operator '" + tokenString + "'");
	}

	private boolean arrayOpenOrStructureSeparatorNotAllowed() {
		return !isPreviousTokenType(
			TokenType.BRACE_CLOSE,
			TokenType.VARIABLE_OR_CONSTANT,
			TokenType.ARRAY_CLOSE,
			TokenType.STRING_LITERAL
		);
	}

	private boolean arrayCloseAllowed() {
		return dontMatchPreviousTokenType(
			TokenType.BRACE_OPEN,
			TokenType.INFIX_OPERATOR,
			TokenType.PREFIX_OPERATOR,
			TokenType.FUNCTION,
			TokenType.COMMA,
			TokenType.ARRAY_OPEN
		).orElse(false);
	}

	private boolean prefixOperatorAllowed() {
		return matchPreviousTokenType(
			TokenType.BRACE_OPEN,
			TokenType.INFIX_OPERATOR,
			TokenType.COMMA,
			TokenType.PREFIX_OPERATOR
		).orElse(true);
	}

	private boolean postfixOperatorAllowed() {
		return isPreviousTokenType(
			TokenType.BRACE_CLOSE,
			TokenType.NUMBER_LITERAL,
			TokenType.VARIABLE_OR_CONSTANT,
			TokenType.STRING_LITERAL
		);
	}

	private boolean infixOperatorAllowed() {
		return isPreviousTokenType(
			TokenType.BRACE_CLOSE,
			TokenType.VARIABLE_OR_CONSTANT,
			TokenType.STRING_LITERAL,
			TokenType.POSTFIX_OPERATOR,
			TokenType.NUMBER_LITERAL
		);
	}

	private de.flapdoodle.eval.nparser.Token parseNumberLiteral() throws ParseException {
		char currentChar=get();
		char nextChar = peek(1); //peekNextChar();
		if (currentChar == '0' && (nextChar == 'x' || nextChar == 'X')) {
			return parseHexNumberLiteral();
		} else {
			return parseDecimalNumberLiteral();
		}
	}

	private de.flapdoodle.eval.nparser.Token parseDecimalNumberLiteral() throws ParseException {
		int tokenStartIndex = index;
		StringBuilder tokenValue = new StringBuilder();

		int lastChar = 0;
		boolean scientificNotation = false;
		while (notEof() && isNumberChar(0)) {
			char currentChar = get();
			if (currentChar == 'e' || currentChar == 'E') {
				scientificNotation = true;
			}
			tokenValue.append(currentChar);
			lastChar = currentChar;
			next();
		}
		// illegal scientific format literal
		if (scientificNotation
			&& (lastChar == 'e'
			|| lastChar == 'E'
			|| lastChar == '+'
			|| lastChar == '-'
			|| lastChar == '.')) {
			throw new ParseException(
				de.flapdoodle.eval.nparser.Token.of(tokenStartIndex, tokenValue.toString(), TokenType.NUMBER_LITERAL),
				"Illegal scientific format");
		}
		return de.flapdoodle.eval.nparser.Token.of(tokenStartIndex, tokenValue.toString(), TokenType.NUMBER_LITERAL);
	}

	private de.flapdoodle.eval.nparser.Token parseHexNumberLiteral() {
		int tokenStartIndex = index;
		StringBuilder tokenValue = new StringBuilder();

		// hexadecimal number, consume "0x"
		tokenValue.append(get());
		next();
		tokenValue.append(get());
		next();
		char currentChar;
		while ((currentChar = get()) != 0 && isHexChar(currentChar)) {
			tokenValue.append(currentChar);
			next();
		}
		return de.flapdoodle.eval.nparser.Token.of(tokenStartIndex, tokenValue.toString(), TokenType.NUMBER_LITERAL);
	}

	private de.flapdoodle.eval.nparser.Token parseIdentifier() throws ParseException {
		int tokenStartIndex = index;
		StringBuilder tokenValue = new StringBuilder();
		char currentChar;
		while ((currentChar = get()) != 0 && isIdentifierChar(currentChar)) {
			tokenValue.append(currentChar);
			next();
		}
		String tokenName = tokenValue.toString();

		if (prefixOperatorAllowed() && operatorDictionary.hasOperator(PrefixOperator.class, tokenName)) {
			return de.flapdoodle.eval.nparser.Token.of(
				tokenStartIndex,
				tokenName,
				TokenType.PREFIX_OPERATOR);
		} else if (postfixOperatorAllowed() && operatorDictionary.hasOperator(PostfixOperator.class, tokenName)) {
			return de.flapdoodle.eval.nparser.Token.of(
				tokenStartIndex,
				tokenName,
				TokenType.POSTFIX_OPERATOR);
		} else if (operatorDictionary.hasOperator(InfixOperator.class, tokenName)) {
			return de.flapdoodle.eval.nparser.Token.of(
				tokenStartIndex,
				tokenName,
				TokenType.INFIX_OPERATOR);
		}

		skipBlanks();
		currentChar = get();
		if (currentChar == '(') {
			return de.flapdoodle.eval.nparser.Token.of(tokenStartIndex, tokenName, TokenType.FUNCTION);
		} else {
			return de.flapdoodle.eval.nparser.Token.of(tokenStartIndex, tokenName, TokenType.VARIABLE_OR_CONSTANT);
		}
	}

	de.flapdoodle.eval.nparser.Token parseStringLiteral() throws ParseException {
		int tokenStartIndex = index;
		StringBuilder tokenValue = new StringBuilder();
		// skip starting quote
		next();
		boolean inQuote = true;
		while (inQuote && notEof()) {
			char currentChar = get();
			if (currentChar == '\\') {
				next();
				tokenValue.append(escapeCharacter(get()));
			} else if (currentChar == '"') {
				inQuote = false;
			} else {
				tokenValue.append((char) currentChar);
			}
			next();
		}
		if (inQuote) {
			throw new ParseException(
				tokenStartIndex, index, tokenValue.toString(), "Closing quote not found");
		}
		return Token.of(tokenStartIndex, tokenValue.toString(), TokenType.STRING_LITERAL);
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
				throw new ParseException(
					index, 1, "\\" + (char) character, "Unknown escape character");
		}
	}

	private boolean isNumberStart(int offset) {
		char currentChar = peek(offset);
		if (Character.isDigit(currentChar)) {
			return true;
		}
		return currentChar == '.' && Character.isDigit(peek(offset + 1));
	}

	private boolean isNumberChar(int offset) {
		char currentChar = peek(offset);
		int previousChar = peek(offset-1); //peekPreviousChar();

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

	private boolean isNextCharNumberChar() {
		return hasNext() && isNumberChar(1);
	}

	private static boolean isHexChar(char current) {
		switch (current) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
				return true;
			default:
				return false;
		}
	}

	private static boolean isIdentifierStart(char currentChar) {
		return Character.isLetter(currentChar) || currentChar == '_';
	}

	private static boolean isIdentifierChar(char currentChar) {
		return Character.isLetter(currentChar) || Character.isDigit(currentChar) || currentChar == '_';
	}

	private void skipBlanks() {
		while (notEof() && Character.isWhitespace(get())) {
			index++;
		}
	}

	private void next() {
		if (notEof()) index++;
	}

	private boolean hasNext() {
		return has(1);
	}

	private boolean has(int offset) {
		if (index+offset < 0) return false;
		return index + offset < end;
	}

	private char peek(int offset) {
		return has(offset) ?  chars[index+offset] : 0;
	}

	private boolean eof() {
		return index >= end;
	}

	private boolean notEof() {
		return !eof();
	}

	private char get() {
		return peek(0);
	}
}
