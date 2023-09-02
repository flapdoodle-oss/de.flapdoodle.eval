package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.Evaluateable;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.Parameters;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.data.ValueArray;
import de.flapdoodle.eval.operators.InfixOperator;
import de.flapdoodle.eval.operators.PostfixOperator;
import de.flapdoodle.eval.operators.PrefixOperator;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.parser.TokenType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
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
		void arrayAccess() {
			Node array = anyValueNode("array", Value.of(ValueArray.of(Value.of("foo"))));
			Node index = valueNode(BigDecimal.ZERO);
			ArrayAccessNode node = ArrayAccessNode.of(token("noop", TokenType.ARRAY_INDEX), array, index);
			assertThat(Node.allNodes(node))
				.containsExactly(node, array, index);
		}

		@Test
		void comparableValue() {
			ComparableValueNode<String> node = valueNode("noop");
			assertThat(Node.allNodes(node))
				.singleElement()
				.isEqualTo(node);
		}

		@Test
		void function() {
			ComparableValueNode<String> parameterA = valueNode("a");
			ComparableValueNode<BigDecimal> parameterB = valueNode(BigDecimal.ONE);

			FunctionNode node = functionNode("noop", failOnEverythingFunction(), parameterA, parameterB);
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

		@Test
		void infix() {
			ComparableValueNode<String> left = valueNode("a");
			ComparableValueNode<BigDecimal> right = valueNode(BigDecimal.ONE);

			InfixOperatorNode node = infixNode("noop", failOnEverythingInfix(), left, right);
			assertThat(Node.allNodes(node))
				.containsExactly(node, left, right);
		}

		@Test
		void prefix() {
			ComparableValueNode<String> operand = valueNode("a");

			PrefixOperatorNode node = prefixNode("noop", failOnEverythingPrefix(), operand);
			assertThat(Node.allNodes(node))
				.containsExactly(node, operand);
		}

		@Test
		void postfix() {
			ComparableValueNode<String> operand = valueNode("a");

			PostfixOperatorNode node = postfixNode("noop", failOnEverythingPostfix(), operand);
			assertThat(Node.allNodes(node))
				.containsExactly(node, operand);
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

	private InfixOperator failOnEverythingInfix() {
		return new InfixOperator() {
			@Override
			public Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token operatorToken, Value<?> leftOperand,
				Value<?> rightOperand) throws EvaluationException {
				throw new RuntimeException("fail");
			}
			@Override
			public int getPrecedence() {
				return 0;
			}
			@Override
			public boolean isLeftAssociative() {
				return false;
			}
		};
	}

	private PrefixOperator failOnEverythingPrefix() {
		return new PrefixOperator() {
			@Override
			public Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token operatorToken, Value<?> operand)
				throws EvaluationException {
				throw new RuntimeException("fail");
			}
			@Override
			public int getPrecedence() {
				return 0;
			}
			@Override
			public boolean isLeftAssociative() {
				return false;
			}
		};
	}

	private PostfixOperator failOnEverythingPostfix() {
		return new PostfixOperator() {
			@Override
			public Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token operatorToken, Value<?> operand)
				throws EvaluationException {
				throw new RuntimeException("fail");
			}
			@Override
			public int getPrecedence() {
				return 0;
			}
			@Override
			public boolean isLeftAssociative() {
				return false;
			}
		};
	}

	protected static Evaluateable failOnEverythingFunction() {
		return new Evaluateable() {
			@Override
			public Parameters parameters() {
				return Parameters.of();
			}
			@Override
			public Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, List<Value<?>> arguments)
				throws EvaluationException {
				throw new RuntimeException("fail");
			}
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

	protected static FunctionNode functionNode(String value, Evaluateable function, Node... parameters) {
		return FunctionNode.of(token(value, TokenType.FUNCTION), function, Arrays.asList(parameters));
	}

	protected static InfixOperatorNode infixNode(String value, InfixOperator operator, Node left, Node right) {
		return InfixOperatorNode.of(token(value, TokenType.FUNCTION), operator, left, right);
	}

	protected static PrefixOperatorNode prefixNode(String value, PrefixOperator operator, Node operand) {
		return PrefixOperatorNode.of(token(value, TokenType.FUNCTION), operator, operand);
	}

	protected static PostfixOperatorNode postfixNode(String value, PostfixOperator operator, Node operand) {
		return PostfixOperatorNode.of(token(value, TokenType.FUNCTION), operator, operand);
	}

	protected static StructureAccessNode structureAccessNode(String value, Node structure, String property) {
		return StructureAccessNode.of(token(value, TokenType.STRUCTURE_SEPARATOR), structure, token(property, TokenType.VARIABLE_OR_CONSTANT));
	}

	protected static Token token(String value, TokenType type) {
		return Token.of(ThreadLocalRandom.current().nextInt(), value, type);
	}

}