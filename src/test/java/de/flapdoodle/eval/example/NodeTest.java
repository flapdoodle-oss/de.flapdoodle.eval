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
package de.flapdoodle.eval.example;

import de.flapdoodle.eval.core.Expression;
import de.flapdoodle.eval.core.ImmutableExpressionFactory;
import de.flapdoodle.eval.core.evaluables.Evaluated;
import de.flapdoodle.eval.core.evaluables.TypedEvaluableByArguments;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.exceptions.ParseException;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.core.parser.TokenType;
import de.flapdoodle.eval.core.tree.*;
import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("core")
class NodeTest {

	@Nested
	class AllNodes {
		@Test
		void anyTypeValue() {
			ValueNode<Value.StringValue> node = anyValueNode("foo", Value.of("bar"));
			assertThat(Node.allNodes(node))
				.singleElement()
				.isEqualTo(node);
		}

		@Test
		void comparableValue() {
			ValueNode<Value.StringValue> node = valueNode("noop");
			assertThat(Node.allNodes(node))
				.singleElement()
				.isEqualTo(node);
		}

		@Test
		void evaluatable() {
			ValueNode<Value.StringValue> parameterA = valueNode("a");
			ValueNode<Value.NumberValue> parameterB = valueNode(BigDecimal.ONE);

			EvaluatableNode node = evaluatableNode("noop", failOnEverythingEvaluatable(), parameterA, parameterB);
			assertThat(Node.allNodes(node))
				.containsExactly(node, parameterA, parameterB);
		}
	}

	@Test
	public void usedVars() {
		Set<String> variables = Node.usedVariables(Arrays.asList(
			valueNode("a"),
			LookupNode.of(token("var", TokenType.STRING_LITERAL))));

		assertThat(variables)
			.containsExactly("var");
	}

	@Test
	public void hashedUsedVars() throws ParseException, EvaluationException {
		ImmutableExpressionFactory factory = Defaults.expressionFactory();

		Expression expression = factory.parse("a*2(x-1000)+c");
		VariableNames variables = Node.hashedUsedVariables(expression.source(), expression.root());
		assertThat(variables.names())
			.containsExactly("a", "x", "c");
		assertThat(variables.nameHashMap())
			.containsExactly(
				MapEntry.entry("a",1546),
				MapEntry.entry("x",47022359),
				MapEntry.entry("c",556969190)
			);

		Expression secondExpression = factory.parse("b*2(KY-1000)+abc");
		VariableNames secondVariables = Node.hashedUsedVariables(secondExpression.source(), secondExpression.root());
		assertThat(secondVariables.names())
			.containsExactly("b", "KY", "abc");
		assertThat(secondVariables.hashOf("b")).isEqualTo(variables.hashOf("a"));
		assertThat(secondVariables.hashOf("KY")).isEqualTo(variables.hashOf("x"));
	}

	protected static TypedEvaluableByArguments failOnEverythingEvaluatable() {
		return values -> {
			throw new RuntimeException("fail");
		};
	}

	protected static <T> ValueNode<T> anyValueNode(String tokenValue, T value) {
		return ValueNode.of(token(tokenValue, TokenType.VARIABLE_OR_CONSTANT), Evaluated.value(value));
	}

	protected static ValueNode<Value.NumberValue> valueNode(BigDecimal value) {
		return ValueNode.of(token(value.toString(), TokenType.NUMBER_LITERAL), Evaluated.value(Value.of(value)));
	}

	protected static ValueNode<Value.StringValue> valueNode(String value) {
		return ValueNode.of(token(value, TokenType.NUMBER_LITERAL), Evaluated.value(Value.of(value)));
	}

	protected static EvaluatableNode evaluatableNode(String value, TypedEvaluableByArguments function, Node... parameters) {
		return EvaluatableNode.of(token(value, TokenType.FUNCTION), function, Arrays.asList(parameters), Defaults.exceptionMapper());
	}

	protected static Token token(String value, TokenType type) {
		return Token.of(ThreadLocalRandom.current().nextInt(), value, type);
	}

}