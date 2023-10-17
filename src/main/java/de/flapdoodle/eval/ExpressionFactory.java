package de.flapdoodle.eval;

import de.flapdoodle.eval.config.Defaults;
import de.flapdoodle.eval.evaluables.*;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.*;
import de.flapdoodle.eval.tree.*;
import de.flapdoodle.eval.values.MapBasedValueResolver;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

	protected abstract ValueResolver constants();
	protected abstract TypedEvaluableByName evaluatables();
	protected abstract TypedEvaluableByNumberOfArguments arrayAccess();
	protected abstract TypedEvaluableByNumberOfArguments propertyAccess();

	protected abstract OperatorMap operatorMap();

	@org.immutables.value.Value.Auxiliary
	public final ImmutableExpressionFactory withConstant(String name, Value<?> value) {
		return ImmutableExpressionFactory.copyOf(this)
				.withConstants(MapBasedValueResolver.empty()
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
				result = ComparableValueNode.of(token, numberOfString(token.value(), mathContext()));
				break;
			case STRING_LITERAL:
				result = ComparableValueNode.of(token, Value.of(token.value()));
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
			return EvaluatableNode.of(token, evaluatable.get(), parameters);
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
			return AnyTypeValueNode.of(token, result);
		}
		return ValueLookup.of(token);
	}

	private Node evaluateFunction(ASTNode startNode, Token token) throws EvaluationException {
		List<Node> parameterResults = new ArrayList<>();
		for (int i = 0; i < startNode.getParameters().size(); i++) {
			parameterResults.add(map(startNode.getParameters().get(i)));
		}

		Optional<? extends TypedEvaluableByArguments> evaluatable = evaluatables().find(token.value(),
			startNode.getParameters().size());

		if (!evaluatable.isPresent()) throw new EvaluationException(token, "could not find evaluatable");

		return EvaluatableNode.of(token, evaluatable.get(), parameterResults);
	}

	private Node evaluateArrayIndex(ASTNode startNode) throws EvaluationException {
		Node objectNode = map(startNode.getParameters().get(0));
		Node indexNode = map(startNode.getParameters().get(1));
		Optional<? extends TypedEvaluableByArguments> arrayAccess = arrayAccess().filterByNumberOfArguments(2);

		if (!arrayAccess.isPresent()) throw new EvaluationException(startNode.getToken(), "could not find array access");

		return EvaluatableNode.of(startNode.getToken(), arrayAccess.get(), Arrays.asList(objectNode, indexNode));
	}

	private Node evaluateStructureSeparator(ASTNode startNode) throws EvaluationException {
		Node structure = map(startNode.getParameters().get(0));
		Token nameToken = startNode.getParameters().get(1).getToken();
		Node name = AnyTypeValueNode.of(nameToken, Value.of(nameToken.value()));

		Optional<? extends TypedEvaluableByArguments> propertyAccess = propertyAccess().filterByNumberOfArguments(2);

		if (!propertyAccess.isPresent()) throw new EvaluationException(startNode.getToken(), "could not find property access");

		return EvaluatableNode.of(startNode.getToken(), propertyAccess.get(), Arrays.asList(structure, name));
	}

	private static Value.NumberValue numberOfString(String value, MathContext mathContext) {
		if (value.startsWith("0x") || value.startsWith("0X")) {
			BigInteger hexToInteger = new BigInteger(value.substring(2), 16);
			return Value.of(new BigDecimal(hexToInteger, mathContext));
		} else {
			return Value.of(new BigDecimal(value, mathContext));
		}
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
			.operatorMap(Defaults.operatorMap())
			.build();
	}

}
