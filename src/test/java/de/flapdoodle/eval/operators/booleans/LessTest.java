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
package de.flapdoodle.eval.operators.booleans;

import de.flapdoodle.eval.BaseEvaluationTest;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.parser.ParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LessTest extends BaseEvaluationTest {

  @ParameterizedTest
  @CsvSource(
      delimiter = ':',
      value = {
        "1<1 : false",
        "2<1 : false",
        "1<2 : true",
        "2.1<2.0 : false",
        "2.0<2.1 : true",
        "2.0<2.0 : false",
        "21.678<21.679 : true",
        "\"abc\"<\"abd\" : true",
        "\"abc\"<\"xyz\" : true",
        "\"abc\"<\"ABC\" : false",
        "\"5\"<\"9\" : true",
        "-5<-4 :true",
        "DT_DATE_TIME(2022,10,30)<DT_DATE_TIME(2022,10,30) : false",
        "DT_DATE_TIME(2022,10,30)<DT_DATE_TIME(2022,10,28) : false",
        "DT_DATE_TIME(2022,10,30)<DT_DATE_TIME(2022,10,31) : true",
        "DT_DURATION_PARSE(\"P2D\")<DT_DURATION_PARSE(\"PT24H\") : false"
      })
  void testInfixLessLiterals(String expression, String expectedResult)
      throws EvaluationException, ParseException {
    assertExpressionHasExpectedResult(expression, expectedResult);
  }
}