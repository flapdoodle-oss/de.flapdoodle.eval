/**
 * Copyright (C) 2023
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.eval.parser;

import org.junit.jupiter.api.Test;

class TokenizerVariableNameTest extends BaseParserTest {

  @Test
  void testSimple() throws ParseException {
    assertAllTokensParsedCorrectly("a", Token.of(1, "a", TokenType.VARIABLE_OR_CONSTANT));
  }

  @Test
  void testEndWithNumber() throws ParseException {
    assertAllTokensParsedCorrectly("var1", Token.of(1, "var1", TokenType.VARIABLE_OR_CONSTANT));
  }

  @Test
  void testContainsNumber() throws ParseException {
    assertAllTokensParsedCorrectly(
        "var2test", Token.of(1, "var2test", TokenType.VARIABLE_OR_CONSTANT));
  }

  @Test
  void testUnderscore() throws ParseException {
    assertAllTokensParsedCorrectly(
        "_var_2_", Token.of(1, "_var_2_", TokenType.VARIABLE_OR_CONSTANT));
  }

  @Test
  void testUmlaut() throws ParseException {
    assertAllTokensParsedCorrectly("Grün", Token.of(1, "Grün", TokenType.VARIABLE_OR_CONSTANT));
    assertAllTokensParsedCorrectly(
        "olá_enchanté_γεια_σας",
      Token.of(1, "olá_enchanté_γεια_σας", TokenType.VARIABLE_OR_CONSTANT));
  }

  @Test
  void testSpecialAlphabetical() throws ParseException {
    assertAllTokensParsedCorrectly(
        "olá_enchanté_γεια_σας",
      Token.of(1, "olá_enchanté_γεια_σας", TokenType.VARIABLE_OR_CONSTANT));
  }
}
