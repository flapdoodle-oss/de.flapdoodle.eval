package de.flapdoodle.eval;

import de.flapdoodle.eval.config.Defaults;
import de.flapdoodle.eval.evaluables.*;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.*;
import de.flapdoodle.eval.tree.EvaluatableNode;
import de.flapdoodle.eval.tree.LookupNode;
import de.flapdoodle.eval.tree.Node;
import de.flapdoodle.eval.tree.ValueNode;

import java.math.MathContext;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@org.immutables.value.Value.Immutable
public abstract class ExpressionFactory {
	@org.immutables.value.Value.Default
	protected MathContext mathContext() {
		return MathContext.DECIMAL128;
	}

	@org.immutables.value.Value.Default
	protected ZoneId zoneId() {
		return ZoneId.systemDefault();
	}

	protected abstract VariableResolver constants();
	protected abstract TypedEvaluableByName evaluatables();
	protected abstract TypedEvaluableByNumberOfArguments arrayAccess();
	protected abstract TypedEvaluableByNumberOfArguments propertyAccess();
	protected abstract BiFunction<String, MathContext, Object> parseNumber();
	protected abstract Function<String, Object> stringAsValue();
	protected abstract Function<EvaluationException, Object> exceptionAsParameter();
	protected abstract Function<Object, Optional<EvaluationException>> matchException();

	protected abstract OperatorMap operatorMap();

	@org.immutables.value.Value.Auxiliary
	public final ImmutableExpressionFactory withConstant(String name, Object value) {
		return ImmutableExpressionFactory.copyOf(this)
				.withConstants(MapBasedVariableResolver.empty()
						.with(name, value)
						.andThen(constants()));
	}

	@org.immutables.value.Value.Auxiliary
	public Expression parse(String expression) throws ParseException, EvaluationException {
		Node node = map(abstractSyntaxTree(expression));
		return Expression.builder()
			.mathContext(mathContext())
			.zoneId(zoneId())
			.root(node)
			.build();
	}

	// VisibleForTests
	@org.immutables.value.Value.Auxiliary
	public ASTNode abstractSyntaxTree(String expression) throws ParseException {
		return new ShuntingYardConverter(expression, tokens(expression), operatorMap(), evaluatables())
			.toAbstractSyntaxTree();
	}

	// VisibleForTests
	@org.immutables.value.Value.Auxiliary
	public List<Token> tokens(String expression) throws ParseException {
		return new Tokenizer(expression, operatorMap()).parse();
	}

	@org.immutables.value.Value.Auxiliary
	protected Node map(ASTNode startNode) throws EvaluationException {
		Node result;
		Token token = startNode.getToken();
		switch (token.type()) {
			case NUMBER_LITERAL:
				result = ValueNode.of(token, parseNumber().apply(token.value(), mathContext()));
				break;
			case STRING_LITERAL:
				result = ValueNode.of(token, stringAsValue().apply(token.value()));
				break;
			case VARIABLE_OR_CONSTANT:
				result = getVariableOrConstant(token);
				break;
			case PREFIX_OPERATOR:
				result = prefixOperator(startNode, token);
				break;
			case POSTFIX_OPERATOR:
				result = postfixOperator(startNode, token);
				break;
			case INFIX_OPERATOR:
				result = infixOperator(startNode, token);
				break;
			case ARRAY_INDEX:
				result = evaluateArrayIndex(startNode);
				break;
			case STRUCTURE_SEPARATOR:
				result = evaluateStructureSeparator(startNode);
				break;
			case FUNCTION:
				result = evaluateFunction(startNode, token);
				break;
			default:
				throw new EvaluationException(token, "Unexpected evaluation token: " + token);
		}
		return result;
	}

	private Node postfixOperator(ASTNode startNode, Token token) throws EvaluationException {
		List<Node> parameters = Arrays.asList(map(startNode.getParameters().get(0)));
		Optional<OperatorMapping> operatorMapping = operatorMap().postfixOperator(token.value());

		if (!operatorMapping.isPresent()) throw new EvaluationException(token, "could not find postfix operator");

		return evaluatableNode(token, operatorMapping.get(), parameters);
	}

	private EvaluatableNode evaluatableNode(Token token, OperatorMapping operatorMapping, List<Node> parameters) {
		Optional<? extends TypedEvaluableByArguments> evaluatable = evaluatables().find(operatorMapping.evaluatable(), parameters.size());
		if (evaluatable.isPresent()) {
			return EvaluatableNode.of(token, evaluatable.get(), parameters, exceptionAsParameter(), matchException());
		} else {
			throw new RuntimeException("could not find evaluatable for "+ operatorMapping);
		}
	}

	private Node infixOperator(ASTNode startNode, Token token) throws EvaluationException {
		Node first = map(startNode.getParameters().get(0));
		Node second = map(startNode.getParameters().get(1));
		List<Node> parameters = Arrays.asList(first, second);
		Optional<OperatorMapping> operatorMapping = operatorMap().infixOperator(token.value());

		if (!operatorMapping.isPresent()) throw new EvaluationException(token, "could not find infix operator");

		return evaluatableNode(token, operatorMapping.get(), parameters);
	}

	private Node prefixOperator(ASTNode startNode, Token token) throws EvaluationException {
		List<Node> parameters = Arrays.asList(map(startNode.getParameters().get(0)));
		Optional<OperatorMapping> operatorMapping = operatorMap().prefixOperator(token.value());

		if (!operatorMapping.isPresent()) throw new EvaluationException(token, "could not find prefix operator");

		return evaluatableNode(token, operatorMapping.get(), parameters);
	}

	private Node getVariableOrConstant(Token token) {
		Object result = constants().get(token.value());
		if (result!=null) {
			return ValueNode.of(token, result);
		}
		return LookupNode.of(token);
	}

	private Node evaluateFunction(ASTNode startNode, Token token) throws EvaluationException {
		List<Node> parameterResults = new ArrayList<>();
		for (int i = 0; i < startNode.getParameters().size(); i++) {
			parameterResults.add(map(startNode.getParameters().get(i)));
		}

		Optional<? extends TypedEvaluableByArguments> evaluatable = evaluatables().find(token.value(),
			startNode.getParameters().size());

		if (!evaluatable.isPresent()) throw new EvaluationException(token, "could not find evaluatable");

		return EvaluatableNode.of(token, evaluatable.get(), parameterResults, exceptionAsParameter(), matchException());
	}

	private Node evaluateArrayIndex(ASTNode startNode) throws EvaluationException {
		Node objectNode = map(startNode.getParameters().get(0));
		Node indexNode = map(startNode.getParameters().get(1));
		Optional<? extends TypedEvaluableByArguments> arrayAccess = arrayAccess().filterByNumberOfArguments(2);

		if (!arrayAccess.isPresent()) throw new EvaluationException(startNode.getToken(), "could not find array access");

		return EvaluatableNode.of(startNode.getToken(), arrayAccess.get(), Arrays.asList(objectNode, indexNode), exceptionAsParameter(), matchException());
	}

	private Node evaluateStructureSeparator(ASTNode startNode) throws EvaluationException {
		Node structure = map(startNode.getParameters().get(0));
		Token nameToken = startNode.getParameters().get(1).getToken();
		Node name = ValueNode.of(nameToken, stringAsValue().apply(nameToken.value()));

		Optional<? extends TypedEvaluableByArguments> propertyAccess = propertyAccess().filterByNumberOfArguments(2);

		if (!propertyAccess.isPresent()) throw new EvaluationException(startNode.getToken(), "could not find property access");

		return EvaluatableNode.of(startNode.getToken(), propertyAccess.get(), Arrays.asList(structure, name), exceptionAsParameter(), matchException());
	}

	public static ImmutableExpressionFactory.Builder builder() {
		return ImmutableExpressionFactory.builder();
	}

	public static ImmutableExpressionFactory defaults() {
		return builder()
			.constants(Defaults.constants())
			.evaluatables(Defaults.evaluatables())
			.arrayAccess(Defaults.arrayAccess())
			.propertyAccess(Defaults.propertyAccess())
			.parseNumber(Defaults::numberFromString)
			.stringAsValue(Defaults::valueFromString)
			.operatorMap(Defaults.operatorMap())
			.exceptionAsParameter(Defaults::exceptionAsParameter)
			.matchException(Defaults::matchException)
			.build();
	}

}
