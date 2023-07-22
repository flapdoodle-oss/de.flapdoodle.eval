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

public enum TokenType {
	BRACE_OPEN,
	BRACE_CLOSE,
	COMMA,
	STRING_LITERAL,
	NUMBER_LITERAL,
	VARIABLE_OR_CONSTANT,
	INFIX_OPERATOR,
	PREFIX_OPERATOR,
	POSTFIX_OPERATOR,
	FUNCTION,
	FUNCTION_PARAM_START,
	ARRAY_OPEN,
	ARRAY_CLOSE,
	ARRAY_INDEX,
	STRUCTURE_SEPARATOR
}
