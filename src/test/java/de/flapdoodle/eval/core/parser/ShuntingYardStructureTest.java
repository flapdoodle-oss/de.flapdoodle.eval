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
package de.flapdoodle.eval.core.parser;

import de.flapdoodle.eval.core.exceptions.ParseException;
import de.flapdoodle.eval.example.Defaults;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShuntingYardStructureTest extends BaseParserTest {

	@Test
	void testSimpleStructure() throws ParseException {
		assertASTTreeIsEqualTo(
			"a.b",
			"{\"type\":\"STRUCTURE_SEPARATOR\",\"value\":\".\",\"children\":[{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"a\"},{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"b\"}]}");
	}

	@Test
	void testTripleStructure() throws ParseException {
		assertASTTreeIsEqualTo(
			"a.b.c",
			"{\"type\":\"STRUCTURE_SEPARATOR\",\"value\":\".\",\"children\":[{\"type\":\"STRUCTURE_SEPARATOR\",\"value\":\".\",\"children\":[{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"a\"},{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"b\"}]},{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"c\"}]}");
	}

	@Test
	void testArrayCombination() throws ParseException {
		assertASTTreeIsEqualTo(
			"order[4].position[2].amount",
			"{\"type\":\"STRUCTURE_SEPARATOR\",\"value\":\".\",\"children\":[{\"type\":\"ARRAY_INDEX\",\"value\":\"[\",\"children\":[{\"type\":\"STRUCTURE_SEPARATOR\",\"value\":\".\",\"children\":[{\"type\":\"ARRAY_INDEX\",\"value\":\"[\",\"children\":[{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"order\"},{\"type\":\"NUMBER_LITERAL\",\"value\":\"4\"}]},{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"position\"}]},{\"type\":\"NUMBER_LITERAL\",\"value\":\"2\"}]},{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"amount\"}]}");
	}

	@Test
	void testExceptionStructureEnd() {
		assertThatThrownBy(() -> Defaults.expressionFactory().parse("a."))
			.isInstanceOf(ParseException.class)
			.hasMessage("Missing second operand for operator");
	}

	@Test
	void testExceptionStructureStart() {
		assertThatThrownBy(() -> Defaults.expressionFactory().parse(".a"))
			.isInstanceOf(ParseException.class)
			.hasMessage("Structure separator not allowed here");
	}
}
