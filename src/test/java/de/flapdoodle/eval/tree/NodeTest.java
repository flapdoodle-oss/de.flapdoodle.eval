package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.parser.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Tag("core")
class NodeTest {

	@Test
	void allNodesAsExpected() {
		ComparableValueNode<String> node = ComparableValueNode.of(Token.of(0, "noop", TokenType.NUMBER_LITERAL), Value.of("foo"));
		assertThat(Node.allNodes(node))
			.singleElement()
			.isEqualTo(node);
	}
}