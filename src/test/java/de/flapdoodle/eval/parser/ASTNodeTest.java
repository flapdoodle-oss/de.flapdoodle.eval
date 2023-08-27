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

import de.flapdoodle.eval.CommonToken;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ASTNodeTest {
	final CommonToken variable = CommonToken.of(1, "variable", TokenType.VARIABLE_OR_CONSTANT);

	@Test
	void testJSONSingle() {
		ASTNode node = ASTNode.of(variable);

		assertThat(node.toJSON())
			.isEqualTo("{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"variable\"}");
	}

	@Test
	void testJSONPrefix() {
		CommonToken token = CommonToken.of(1, "-", TokenType.PREFIX_OPERATOR);
		ASTNode node = ASTNode.of(token, ASTNode.of(variable));

		assertThat(node.toJSON())
			.isEqualTo(
				"{\"type\":\"PREFIX_OPERATOR\",\"value\":\"-\",\"children\":[{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"variable\"}]}");
	}

	@Test
	void testJSONInfix() {
		CommonToken token = CommonToken.of(1, "+", TokenType.INFIX_OPERATOR);
		ASTNode node = ASTNode.of(token, ASTNode.of(variable), ASTNode.of(variable));

		assertThat(node.toJSON())
			.isEqualTo(
				"{\"type\":\"INFIX_OPERATOR\",\"value\":\"+\",\"children\":[{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"variable\"},{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"variable\"}]}");
	}

	@Test
	void testJSONFunction() {
		CommonToken token = CommonToken.of(1, "+", TokenType.FUNCTION);
		ASTNode node =
			ASTNode.of(token, ASTNode.of(variable), ASTNode.of(variable), ASTNode.of(variable));

		assertThat(node.toJSON())
			.isEqualTo(
				"{\"type\":\"FUNCTION\",\"value\":\"+\",\"children\":[{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"variable\"},{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"variable\"},{\"type\":\"VARIABLE_OR_CONSTANT\",\"value\":\"variable\"}]}");
	}
}
