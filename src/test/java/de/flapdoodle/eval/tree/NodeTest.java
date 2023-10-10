package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.evaluatables.TypedEvaluatableByArguments;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.parser.TokenType;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.Values;
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
			AnyTypeValueNode<String> node = anyValueNode("foo", Value.of("bar"));
			assertThat(Node.allNodes(node))
				.singleElement()
				.isEqualTo(node);
		}

		@Test
		void comparableValue() {
			ComparableValueNode<String> node = valueNode("noop");
			assertThat(Node.allNodes(node))
				.singleElement()
				.isEqualTo(node);
		}

		@Test
		void evaluatable() {
			ComparableValueNode<String> parameterA = valueNode("a");
			ComparableValueNode<BigDecimal> parameterB = valueNode(BigDecimal.ONE);

			EvaluatableNode node = evaluatableNode("noop", failOnEverythingEvaluatable(), parameterA, parameterB);
			assertThat(Node.allNodes(node))
				.containsExactly(node, parameterA, parameterB);
		}

		@Test
		void structureAccess() {
			AnyTypeValueNode<String> structure = anyValueNode("foo", Value.of("bar"));
			StructureAccessNode node = structureAccessNode("noop", structure, "property");
			assertThat(Node.allNodes(node))
				.containsExactly(node, structure);
		}
	}

	@Test
	public void usedVars() {
		Set<String> variables = Node.usedVariables(Arrays.asList(
			valueNode("a"),
			ValueLookup.of(token("var", TokenType.STRING_LITERAL))));

		assertThat(variables)
			.containsExactly("var");
	}

	protected static TypedEvaluatableByArguments failOnEverythingEvaluatable() {
		return values -> {
			throw new RuntimeException("fail");
		};
	}

	protected static <T> AnyTypeValueNode<T> anyValueNode(String tokenValue, Value<T> value) {
		return AnyTypeValueNode.of(token(tokenValue, TokenType.VARIABLE_OR_CONSTANT), value);
	}

	protected static ComparableValueNode<BigDecimal> valueNode(BigDecimal value) {
		return ComparableValueNode.of(token(value.toString(), TokenType.NUMBER_LITERAL), Value.of(value));
	}

	protected static ComparableValueNode<String> valueNode(String value) {
		return ComparableValueNode.of(token(value, TokenType.NUMBER_LITERAL), Value.of(value));
	}

	protected static EvaluatableNode evaluatableNode(String value, TypedEvaluatableByArguments function, Node... parameters) {
		return EvaluatableNode.of(token(value, TokenType.FUNCTION), function, Arrays.asList(parameters));
	}

	protected static StructureAccessNode structureAccessNode(String value, Node structure, String property) {
		return StructureAccessNode.of(token(value, TokenType.STRUCTURE_SEPARATOR), structure, token(property, TokenType.VARIABLE_OR_CONSTANT));
	}

	protected static Token token(String value, TokenType type) {
		return Token.of(ThreadLocalRandom.current().nextInt(), value, type);
	}

}