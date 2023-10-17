package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.config.Defaults;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.core.parser.TokenType;
import de.flapdoodle.eval.core.tree.EvaluatableNode;
import de.flapdoodle.eval.core.tree.LookupNode;
import de.flapdoodle.eval.core.tree.Node;
import de.flapdoodle.eval.core.tree.ValueNode;
import de.flapdoodle.eval.evaluables.TypedEvaluableByArguments;
import de.flapdoodle.eval.values.Value;
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

	protected static TypedEvaluableByArguments failOnEverythingEvaluatable() {
		return values -> {
			throw new RuntimeException("fail");
		};
	}

	protected static <T> ValueNode<T> anyValueNode(String tokenValue, T value) {
		return ValueNode.of(token(tokenValue, TokenType.VARIABLE_OR_CONSTANT), value);
	}

	protected static ValueNode<Value.NumberValue> valueNode(BigDecimal value) {
		return ValueNode.of(token(value.toString(), TokenType.NUMBER_LITERAL), Value.of(value));
	}

	protected static ValueNode<Value.StringValue> valueNode(String value) {
		return ValueNode.of(token(value, TokenType.NUMBER_LITERAL), Value.of(value));
	}

	protected static EvaluatableNode evaluatableNode(String value, TypedEvaluableByArguments function, Node... parameters) {
		return EvaluatableNode.of(token(value, TokenType.FUNCTION), function, Arrays.asList(parameters), Defaults.exceptionMapper());
	}

	protected static Token token(String value, TokenType type) {
		return Token.of(ThreadLocalRandom.current().nextInt(), value, type);
	}

}