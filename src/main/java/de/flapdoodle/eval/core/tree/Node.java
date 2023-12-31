package de.flapdoodle.eval.core.tree;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Node {
    @org.immutables.value.Value.Parameter
    public abstract Token token();

    @org.immutables.value.Value.Auxiliary
    public abstract Object evaluate(VariableResolver variableResolver, EvaluationContext context) throws EvaluationException;

    public static List<Node> allNodes(Node node) {
        ArrayList<Node> ret = new ArrayList<>();
        ret.add(node);
        List<Node> subnodes = Collections.emptyList();

        if (node instanceof EvaluatableNode) {
            subnodes = ((EvaluatableNode) node).parameters();
        }
        subnodes.forEach(subNode -> ret.addAll(allNodes(subNode)));
        return ret;
    }

    // VisibleForTests
    public static Set<String> usedVariables(List<Node> nodes) {
        return nodes.stream()
          .filter(it -> it instanceof LookupNode)
          .map(it -> it.token().value())
          .collect(Collectors.toSet());
    }

    public static Set<String> usedVariables(Node node) {
        return usedVariables(allNodes(node));
    }
}
