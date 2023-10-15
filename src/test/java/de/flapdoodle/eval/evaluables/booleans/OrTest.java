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
package de.flapdoodle.eval.evaluables.booleans;

import de.flapdoodle.eval.BaseEvaluationTest;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.ParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OrTest extends BaseEvaluationTest {

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
//        "1||1 : true",
//        "1||2 : true",
//        "0||1 : true",
//        "0||0 : false",
//        "22||33 : true",
//        "\"true\"||\"true\" : true",
//        "\"true\"||\"false\" : true",
//        "\"false\"||\"false\" : false",
			"true||true : true",
			"true||false : true",
			"false||false : false",
			"(1==1)||(2==3) : true",
			"(2>4)||(4<6) :true"
		})
	void testInfixLessLiterals(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asBoolean(expectedResult));
	}
}
