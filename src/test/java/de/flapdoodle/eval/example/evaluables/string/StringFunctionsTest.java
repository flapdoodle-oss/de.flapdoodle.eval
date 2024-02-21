/*
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
package de.flapdoodle.eval.example.evaluables.string;

import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.exceptions.ParseException;
import de.flapdoodle.eval.example.BaseEvaluationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StringFunctionsTest extends BaseEvaluationTest {

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"toUpper(\"\") : ''",
			"toUpper(\"a\") : A",
			"toUpper(\"A\") : A",
			"toUpper(\"AbCdEf\") : ABCDEF",
			"toUpper(\"A1b3C4/?\") : A1B3C4/?",
			"toUpper(\"äöüß\") : ÄÖÜSS"
		})
	void testUpper(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asString(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"toLower(\"\") : ''",
			"toLower(\"A\") : a",
			"toLower(\"a\") : a",
			"toLower(\"AbCdEf\") : abcdef",
			"toLower(\"A1b3C4/?\") : a1b3c4/?",
			"toLower(\"ÄÖÜSS\") : äöüss"
		})
	void testLower(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asString(expectedResult));
	}

	@ParameterizedTest
	@CsvSource(
		delimiter = ':',
		value = {
			"contains(\"\", \"\") : true",
			"contains(\"a\", \"a\") : true",
			"contains(\"Hello World\", \"Wor\") : true",
			"contains(\"What a world\", \"what\") : true",
			"contains(\"What a world\", \"a world\") : true",
			"contains(\"What a world\", \"moon\") : false",
			"contains(\"\", \"text\") : false"
		})
	void testContains(String expression, String expectedResult)
		throws EvaluationException, ParseException {
		assertExpressionHasExpectedResult(expression, asBoolean(expectedResult));
	}
}
