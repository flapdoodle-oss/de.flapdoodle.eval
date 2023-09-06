package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Node {
    @org.immutables.value.Value.Parameter
    public abstract Token token();

    @org.immutables.value.Value.Auxiliary
    public abstract Value<?> evaluate(ValueResolver variableResolver, EvaluationContext context) throws EvaluationException;

    public static List<Node> allNodes(Node node) {
        ArrayList<Node> ret = new ArrayList<>();
        ret.add(node);
        List<Node> subnodes = Collections.emptyList();

        if (node instanceof ArrayAccessNode) {
            subnodes = Arrays.asList(
                    ((ArrayAccessNode) node).array(),
                    ((ArrayAccessNode) node).index()
            );
        } else if (node instanceof StructureAccessNode) {
            subnodes = Collections.singletonList(
							((StructureAccessNode) node).structure()
						);
        } else if (node instanceof FunctionNode) {
            subnodes = ((FunctionNode) node).parameters();
        } else if (node instanceof InfixOperatorNode) {
            subnodes = Arrays.asList(
                    ((InfixOperatorNode) node).leftOperand(),
                    ((InfixOperatorNode) node).rightOperand()
            );
        } else if (node instanceof PostfixOperatorNode) {
            subnodes = Collections.singletonList(
							((PostfixOperatorNode) node).operand()
						);
        } else if (node instanceof PrefixOperatorNode) {
            subnodes = Collections.singletonList(
							((PrefixOperatorNode) node).operand()
						);
        }
        subnodes.forEach(subNode -> ret.addAll(allNodes(subNode)));
        return ret;
    }

    // VisibleForTests
    static Set<String> usedVariables(List<Node> nodes) {
        return nodes.stream()
          .filter(it -> it instanceof ValueLookup)
          .map(it -> it.token().value())
          // TODO change to case sensitive vars
          .map(String::toLowerCase)
          .collect(Collectors.toSet());
    }

    public static Set<String> usedVariables(Node node) {
        return usedVariables(allNodes(node));
    }
}
