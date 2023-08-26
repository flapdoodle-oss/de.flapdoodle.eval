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

import de.flapdoodle.eval.Evaluateable;
import de.flapdoodle.eval.config.Configuration;
import de.flapdoodle.eval.config.EvaluateableResolver;
import de.flapdoodle.eval.config.OperatorResolver;
import de.flapdoodle.eval.operators.InfixOperator;
import de.flapdoodle.eval.operators.Operator;
import de.flapdoodle.eval.operators.PostfixOperator;
import de.flapdoodle.eval.operators.PrefixOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * The tokenizer is responsible to parse a string and return a list of tokens. The order of tokens
 * will follow the infix expression notation, skipping any blank characters.
 */
public class Tokenizer {
	private final String expressionString;
	private final char[] chars;
	private final int end;

	private final OperatorResolver operatorDictionary;

	private final EvaluateableResolver functions;

	private final Configuration configuration;

	private final List<Token> tokens = new ArrayList<>();

	private int index = 0;

//	private int currentChar = -2;

	private int braceBalance;

	private int arrayBalance;

	public Tokenizer(String expressionString, Configuration configuration) {
		this.expressionString = expressionString;
		this.chars = expressionString.toCharArray();
		this.end = chars.length;
		
		this.configuration = configuration;
		this.operatorDictionary = configuration.getOperatorResolver();
		this.functions = configuration.functions();
	}

	/**
	 * Parse the given expression and return a list of tokens, representing the expression.
	 *
	 * @return A list of expression tokens.
	 * @throws ParseException When the expression can't be parsed.
	 */
	public List<Token> parse() throws ParseException {

		Token currentToken;
		while ((currentToken = getNextToken()) != null) {
			if (implicitMultiplicationPossible(currentToken)) {
				if (configuration.isImplicitMultiplicationAllowed()) {
					Token multiplication =
						Token.of(
							currentToken.start(),
							"*",
							TokenType.INFIX_OPERATOR,
							operatorDictionary.get(InfixOperator.class, "*"));
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

	private boolean implicitMultiplicationPossible(Token currentToken) {
		Token previousToken = getPreviousToken();

		if (previousToken == null) {
			return false;
		}

		return ((previousToken.type() == TokenType.BRACE_CLOSE && currentToken.type() == TokenType.BRACE_OPEN)
			|| (previousToken.type() == TokenType.NUMBER_LITERAL
			&& currentToken.type() == TokenType.VARIABLE_OR_CONSTANT)
			|| (previousToken.type() == TokenType.NUMBER_LITERAL && currentToken.type() == TokenType.BRACE_OPEN));
	}

	private void validateToken(Token currentToken) throws ParseException {
		Token previousToken = getPreviousToken();
		if (previousToken != null
			&& previousToken.type() == TokenType.INFIX_OPERATOR
			&& invalidTokenAfterInfixOperator(currentToken)) {
			throw new ParseException(currentToken, "Unexpected token after infix operator");
		}
	}

	private boolean invalidTokenAfterInfixOperator(Token token) {
		switch (token.type()) {
			case INFIX_OPERATOR:
			case BRACE_CLOSE:
			case COMMA:
				return true;
			default:
				return false;
		}
	}

	private Token getNextToken() throws ParseException {

		// blanks are always skipped.
		skipBlanks();

		char currentChar=get();
		// end of input
		if (currentChar == 0) {
			return null;
		}

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
			Token token = Token.of(index, ",", TokenType.COMMA);
			consumeChar();
			return token;
		} else if (isAtIdentifierStart()) {
			return parseIdentifier();
		} else if (isAtNumberStart()) {
			return parseNumberLiteral();
		} else {
			return parseOperator();
		}
	}

	private Token parseStructureSeparator() throws ParseException {
		Token token = Token.of(index, ".", TokenType.STRUCTURE_SEPARATOR);
		if (arrayOpenOrStructureSeparatorNotAllowed()) {
			throw new ParseException(token, "Structure separator not allowed here");
		}
		consumeChar();
		return token;
	}

	private Token parseArrayClose() throws ParseException {
		Token token = Token.of(index, "]", TokenType.ARRAY_CLOSE);
		if (!arrayCloseAllowed()) {
			throw new ParseException(token, "Array close not allowed here");
		}
		consumeChar();
		arrayBalance--;
		if (arrayBalance < 0) {
			throw new ParseException(token, "Unexpected closing array");
		}
		return token;
	}

	private Token parseArrayOpen() throws ParseException {
		Token token = Token.of(index, "[", TokenType.ARRAY_OPEN);
		if (arrayOpenOrStructureSeparatorNotAllowed()) {
			throw new ParseException(token, "Array open not allowed here");
		}
		consumeChar();
		arrayBalance++;
		return token;
	}

	private Token parseBraceClose() throws ParseException {
		Token token = Token.of(index, ")", TokenType.BRACE_CLOSE);
		consumeChar();
		braceBalance--;
		if (braceBalance < 0) {
			throw new ParseException(token, "Unexpected closing brace");
		}
		return token;
	}

	private Token parseBraceOpen() {
		Token token = Token.of(index, "(", TokenType.BRACE_OPEN);
		consumeChar();
		braceBalance++;
		return token;
	}

	private Token getPreviousToken() {
		return tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
	}

	private Token parseOperator() throws ParseException {
		int tokenStartIndex = index;
		StringBuilder tokenValue = new StringBuilder();
		while (true) {
			char currentChar=get();
			tokenValue.append((char) currentChar);
			String tokenString = tokenValue.toString();
			String possibleNextOperator = tokenString + peek(1); // (char) peekNextChar();
			boolean possibleNextOperatorFound =
				(prefixOperatorAllowed() && operatorDictionary.hasOperator(PrefixOperator.class, possibleNextOperator))
					|| (postfixOperatorAllowed()
					&& operatorDictionary.hasOperator(PostfixOperator.class, possibleNextOperator))
					|| (infixOperatorAllowed()
					&& operatorDictionary.hasOperator(InfixOperator.class, possibleNextOperator));
			consumeChar();
			if (!possibleNextOperatorFound) {
				break;
			}
		}
		String tokenString = tokenValue.toString();
		if (prefixOperatorAllowed() && operatorDictionary.hasOperator(PrefixOperator.class, tokenString)) {
			Operator operator = operatorDictionary.get(PrefixOperator.class, tokenString);
			return Token.of(tokenStartIndex, tokenString, TokenType.PREFIX_OPERATOR, operator);
		} else if (postfixOperatorAllowed() && operatorDictionary.hasOperator(PostfixOperator.class, tokenString)) {
			Operator operator = operatorDictionary.get(PostfixOperator.class, tokenString);
			return Token.of(tokenStartIndex, tokenString, TokenType.POSTFIX_OPERATOR, operator);
		} else if (operatorDictionary.hasOperator(InfixOperator.class, tokenString)) {
			Operator operator = operatorDictionary.get(InfixOperator.class, tokenString);
			return Token.of(tokenStartIndex, tokenString, TokenType.INFIX_OPERATOR, operator);
		} else if (tokenString.equals(".") && configuration.isStructuresAllowed()) {
			return Token.of(tokenStartIndex, tokenString, TokenType.STRUCTURE_SEPARATOR);
		}
		throw new ParseException(
			tokenStartIndex,
			tokenStartIndex + tokenString.length() - 1,
			tokenString,
			"Undefined operator '" + tokenString + "'");
	}

	private boolean arrayOpenOrStructureSeparatorNotAllowed() {
		Token previousToken = getPreviousToken();

		if (previousToken == null) {
			return true;
		}

		switch (previousToken.type()) {
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
		Token previousToken = getPreviousToken();

		if (previousToken == null) {
			return false;
		}

		switch (previousToken.type()) {
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

	private boolean prefixOperatorAllowed() {
		Token previousToken = getPreviousToken();

		if (previousToken == null) {
			return true;
		}

		switch (previousToken.type()) {
			case BRACE_OPEN:
			case INFIX_OPERATOR:
			case COMMA:
			case PREFIX_OPERATOR:
				return true;
			default:
				return false;
		}
	}

	private boolean postfixOperatorAllowed() {
		Token previousToken = getPreviousToken();

		if (previousToken == null) {
			return false;
		}

		switch (previousToken.type()) {
			case BRACE_CLOSE:
			case NUMBER_LITERAL:
			case VARIABLE_OR_CONSTANT:
			case STRING_LITERAL:
				return true;
			default:
				return false;
		}
	}

	private boolean infixOperatorAllowed() {
		Token previousToken = getPreviousToken();

		if (previousToken == null) {
			return false;
		}

		switch (previousToken.type()) {
			case BRACE_CLOSE:
			case VARIABLE_OR_CONSTANT:
			case STRING_LITERAL:
			case POSTFIX_OPERATOR:
			case NUMBER_LITERAL:
				return true;
			default:
				return false;
		}
	}

	private Token parseNumberLiteral() throws ParseException {
		char currentChar=get();
		char nextChar = peek(1); //peekNextChar();
		if (currentChar == '0' && (nextChar == 'x' || nextChar == 'X')) {
			return parseHexNumberLiteral();
		} else {
			return parseDecimalNumberLiteral();
		}
	}

	private Token parseDecimalNumberLiteral() throws ParseException {
		int tokenStartIndex = index;
		StringBuilder tokenValue = new StringBuilder();

		int lastChar = 0;
		boolean scientificNotation = false;
		char currentChar;
		while ((currentChar = get()) != 0 && isAtNumberChar()) {
			if (currentChar == 'e' || currentChar == 'E') {
				scientificNotation = true;
			}
			tokenValue.append((char) currentChar);
			lastChar = currentChar;
			consumeChar();
		}
		// illegal scientific format literal
		if (scientificNotation
			&& (lastChar == 'e'
			|| lastChar == 'E'
			|| lastChar == '+'
			|| lastChar == '-'
			|| lastChar == '.')) {
			throw new ParseException(
				Token.of(tokenStartIndex, tokenValue.toString(), TokenType.NUMBER_LITERAL),
				"Illegal scientific format");
		}
		return Token.of(tokenStartIndex, tokenValue.toString(), TokenType.NUMBER_LITERAL);
	}

	private Token parseHexNumberLiteral() {
		int tokenStartIndex = index;
		StringBuilder tokenValue = new StringBuilder();

		// hexadecimal number, consume "0x"
		tokenValue.append((char) get());
		consumeChar();
		tokenValue.append((char) get());
		consumeChar();
		char currentChar;
		while ((currentChar = get()) != 0 && isAtHexChar()) {
			tokenValue.append((char) currentChar);
			consumeChar();
		}
		return Token.of(tokenStartIndex, tokenValue.toString(), TokenType.NUMBER_LITERAL);
	}

	private Token parseIdentifier() throws ParseException {
		int tokenStartIndex = index;
		StringBuilder tokenValue = new StringBuilder();
		char currentChar;
		while ((currentChar = get()) != 0 && isAtIdentifierChar()) {
			tokenValue.append((char) currentChar);
			consumeChar();
		}
		String tokenName = tokenValue.toString();

		if (prefixOperatorAllowed() && operatorDictionary.hasOperator(PrefixOperator.class, tokenName)) {
			return Token.of(
				tokenStartIndex,
				tokenName,
				TokenType.PREFIX_OPERATOR,
				operatorDictionary.get(PrefixOperator.class, tokenName));
		} else if (postfixOperatorAllowed() && operatorDictionary.hasOperator(PostfixOperator.class, tokenName)) {
			return Token.of(
				tokenStartIndex,
				tokenName,
				TokenType.POSTFIX_OPERATOR,
				operatorDictionary.get(PostfixOperator.class, tokenName));
		} else if (operatorDictionary.hasOperator(InfixOperator.class, tokenName)) {
			return Token.of(
				tokenStartIndex,
				tokenName,
				TokenType.INFIX_OPERATOR,
				operatorDictionary.get(InfixOperator.class, tokenName));
		}

		skipBlanks();
		currentChar = get();
		if (currentChar == '(') {
			if (!functions.has(tokenName)) {
				throw new ParseException(
					tokenStartIndex,
					index,
					tokenName,
					"Undefined function '" + tokenName + "'");
			}
			Evaluateable function = functions.get(tokenName);
			return Token.of(tokenStartIndex, tokenName, TokenType.FUNCTION, function);
		} else {
			return Token.of(tokenStartIndex, tokenName, TokenType.VARIABLE_OR_CONSTANT);
		}
	}

	Token parseStringLiteral() throws ParseException {
		int tokenStartIndex = index;
		StringBuilder tokenValue = new StringBuilder();
		// skip starting quote
		consumeChar();
		boolean inQuote = true;
		char currentChar;
		while (inQuote && (currentChar = get()) != 0) {
			if (currentChar == '\\') {
				consumeChar();
				tokenValue.append(escapeCharacter(get()));
			} else if (currentChar == '"') {
				inQuote = false;
			} else {
				tokenValue.append((char) currentChar);
			}
			consumeChar();
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

	private boolean isAtNumberStart() {
		char currentChar = get();
		if (Character.isDigit(currentChar)) {
			return true;
		}
		return currentChar == '.' && Character.isDigit(peek(1)/*peekNextChar()*/);
	}

	private boolean isAtNumberChar() {
		char currentChar = get();
		int previousChar = peek(-1); //peekPreviousChar();

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
		if (peek(1)/*peekNextChar()*/ == 0) {
			return false;
		}
		consumeChar();
		boolean isAtNumber = isAtNumberChar();
		index--;
//		currentChar = expressionString.charAt(index - 1);
		return isAtNumber;
	}

	private boolean isAtHexChar() {
		switch (get()) {
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

	private boolean isAtIdentifierStart() {
		char currentChar = get();
		return Character.isLetter(currentChar) || currentChar == '_';
	}

	private boolean isAtIdentifierChar() {
		char currentChar = get();
		return Character.isLetter(currentChar) || Character.isDigit(currentChar) || currentChar == '_';
	}

	private void skipBlanks() {
//		char currentChar = get();
//		if (currentChar == -2) {
//			// consume first character of expression
//			consumeChar();
//		}
		char currentChar;
		while ((currentChar = get()) != 0 && Character.isWhitespace(currentChar)) {
			consumeChar();
		}
	}

//	private char peekNextChar() {
//		return index == end
//			? 0
//			: chars[index];
//	}

	private void consumeChar() {
		if (index == end) {
//			currentChar = 0;
		} else {
//			currentChar = chars[index++];
			index++;
		}
	}

	/***
	 *
	 */

//	protected void skip() throws java.text.ParseException {
//		get();
//	}

	private char peek(int offset) {
		if (index+offset < 0) return 0;
		if (index+offset >= end) return 0;
		return chars[index+offset];
	}

	private char get() {
		return peek(0);
	}

//	protected char get() throws java.text.ParseException {
//		if (index >= end) throw parseException("EOF");
//		return chars[index++];
//	}
//
//	protected char get(String message) throws java.text.ParseException {
//		if (index >= end) throw parseException(message);
//		return chars[index++];
//	}

	protected void skipWS() {
		while (index < end && Character.isWhitespace(chars[index])) {
			index++;
		}
	}

}
