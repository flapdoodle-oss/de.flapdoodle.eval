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
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.ParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PrefixNotTest extends BaseEvaluationTest {

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
//        "!0 : true",
//        "!1 : false",
//        "!2.6 : false",
//        "!-2 : false",
//        "!\"true\" : false",
//        "!\"false\" : true",
			"!true : false",
			"!false : true",
			"!(1==1) : false",
			"!(2==3) : true"
		})
	void testInfixLessLiterals(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asBoolean(expectedResult));
	}
}
