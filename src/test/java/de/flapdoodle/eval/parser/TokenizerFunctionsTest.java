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

import de.flapdoodle.eval.config.TestConfigurationProvider.DummyFunction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenizerFunctionsTest extends BaseParserTest {

  @Test
  void testSimple() throws ParseException {
    configuration = configuration.witFunction("f", new DummyFunction());
    assertAllTokensParsedCorrectly(
        "f(x)",
      Token.of(1, "f", TokenType.FUNCTION),
      Token.of(2, "(", TokenType.BRACE_OPEN),
      Token.of(3, "x", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(4, ")", TokenType.BRACE_CLOSE));
  }

  @Test
  void testBlanks() throws ParseException {
    configuration = configuration.witFunction("f", new DummyFunction());
    assertAllTokensParsedCorrectly(
        "f (x)",
      Token.of(1, "f", TokenType.FUNCTION),
      Token.of(3, "(", TokenType.BRACE_OPEN),
      Token.of(4, "x", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(5, ")", TokenType.BRACE_CLOSE));
  }

  @Test
  void testUnderscores() throws ParseException {
    configuration = configuration.witFunction("_f_x_", new DummyFunction());
    assertAllTokensParsedCorrectly(
        "_f_x_(x)",
      Token.of(1, "_f_x_", TokenType.FUNCTION),
      Token.of(6, "(", TokenType.BRACE_OPEN),
      Token.of(7, "x", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(8, ")", TokenType.BRACE_CLOSE));
  }

  @Test
  void testWithNumbers() throws ParseException {
    configuration = configuration.witFunction("f1x2", new DummyFunction());
    assertAllTokensParsedCorrectly(
        "f1x2(x)",
      Token.of(1, "f1x2", TokenType.FUNCTION),
      Token.of(5, "(", TokenType.BRACE_OPEN),
      Token.of(6, "x", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(7, ")", TokenType.BRACE_CLOSE));
  }

  @Test
  void testWithMoreParameters() throws ParseException {
    assertAllTokensParsedCorrectly(
        "SUM(a, 2, 3)",
      Token.of(1, "SUM", TokenType.FUNCTION),
      Token.of(4, "(", TokenType.BRACE_OPEN),
      Token.of(5, "a", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(6, ",", TokenType.COMMA),
      Token.of(8, "2", TokenType.NUMBER_LITERAL),
      Token.of(9, ",", TokenType.COMMA),
      Token.of(11, "3", TokenType.NUMBER_LITERAL),
      Token.of(12, ")", TokenType.BRACE_CLOSE));
  }

  @Test
  void testWithMixedParameters() throws ParseException {
    assertAllTokensParsedCorrectly(
        "TEST(a, \"hello\", 3)",
      Token.of(1, "TEST", TokenType.FUNCTION),
      Token.of(5, "(", TokenType.BRACE_OPEN),
      Token.of(6, "a", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(7, ",", TokenType.COMMA),
      Token.of(9, "hello", TokenType.STRING_LITERAL),
      Token.of(16, ",", TokenType.COMMA),
      Token.of(18, "3", TokenType.NUMBER_LITERAL),
      Token.of(19, ")", TokenType.BRACE_CLOSE));
  }

  @Test
  void testFunctionInParameter() throws ParseException {
    assertAllTokensParsedCorrectly(
        "TEST(a, FACT(x), 3)",
      Token.of(1, "TEST", TokenType.FUNCTION),
      Token.of(5, "(", TokenType.BRACE_OPEN),
      Token.of(6, "a", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(7, ",", TokenType.COMMA),
      Token.of(9, "FACT", TokenType.FUNCTION),
      Token.of(13, "(", TokenType.BRACE_OPEN),
      Token.of(14, "x", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(15, ")", TokenType.BRACE_CLOSE),
      Token.of(16, ",", TokenType.COMMA),
      Token.of(18, "3", TokenType.NUMBER_LITERAL),
      Token.of(19, ")", TokenType.BRACE_CLOSE));
  }

  @Test
  void testFunctionInParameterInFunctionParameter() throws ParseException {
    assertAllTokensParsedCorrectly(
        "SUM(a,FACT(MIN(x,y)),3)",
      Token.of(1, "SUM", TokenType.FUNCTION),
      Token.of(4, "(", TokenType.BRACE_OPEN),
      Token.of(5, "a", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(6, ",", TokenType.COMMA),
      Token.of(7, "FACT", TokenType.FUNCTION),
      Token.of(11, "(", TokenType.BRACE_OPEN),
      Token.of(12, "MIN", TokenType.FUNCTION),
      Token.of(15, "(", TokenType.BRACE_OPEN),
      Token.of(16, "x", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(17, ",", TokenType.COMMA),
      Token.of(18, "y", TokenType.VARIABLE_OR_CONSTANT),
      Token.of(19, ")", TokenType.BRACE_CLOSE),
      Token.of(20, ")", TokenType.BRACE_CLOSE),
      Token.of(21, ",", TokenType.COMMA),
      Token.of(22, "3", TokenType.NUMBER_LITERAL),
      Token.of(23, ")", TokenType.BRACE_CLOSE));
  }

  @Test
  void testUndefinedFunction() {
    assertThatThrownBy(() -> new Tokenizer("a(b)", configuration).parse())
        .isEqualTo(new ParseException(1, 2, "a", "Undefined function 'a'"));
  }
}
