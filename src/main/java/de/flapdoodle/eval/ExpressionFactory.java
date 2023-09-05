package de.flapdoodle.eval;

import de.flapdoodle.eval.config.*;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.InfixOperator;
import de.flapdoodle.eval.operators.Operator;
import de.flapdoodle.eval.operators.PostfixOperator;
import de.flapdoodle.eval.operators.PrefixOperator;
import de.flapdoodle.eval.parser.*;
import de.flapdoodle.eval.tree.*;
import de.flapdoodle.types.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@org.immutables.value.Value.Immutable
public abstract class ExpressionFactory {
	protected abstract MathContext mathContext();
	protected abstract ZoneId zoneId();

	protected abstract ValueResolver constants();
	protected abstract OperatorResolver operators();
	protected abstract EvaluateableResolver functions();

	@SafeVarargs
	@org.immutables.value.Value.Auxiliary
	public final ImmutableExpressionFactory withFunctions(Pair<String, ? extends Evaluateable>... functions) {
		return ImmutableExpressionFactory.copyOf(this)
			.withFunctions(MapBasedEvaluateableResolver.of(functions)
				.andThen(functions()));
	}

	@SafeVarargs
	@org.immutables.value.Value.Auxiliary
	public final ImmutableExpressionFactory withOperators(Pair<String, Operator>... operators) {
		ImmutableMapBasedOperatorResolver newOperatorResolver = MapBasedOperatorResolver.of(operators);
		return ImmutableExpressionFactory.copyOf(this)
			.withOperators(newOperatorResolver
				.andThen(operators()));
	}

	@org.immutables.value.Value.Auxiliary
	public final ImmutableExpressionFactory withConstant(String name, Value<?> value) {
		return ImmutableExpressionFactory.copyOf(this)
				.withConstants(MapBasedValueResolver.empty()
						.with(name, value)
						.andThen(constants()));
	}

	@org.immutables.value.Value.Auxiliary
	public ParsedExpression parse(String expression) throws ParseException, EvaluationException {
		Node node = map(abstractSyntaxTree(expression));
		return ParsedExpression.builder()
			.mathContext(mathContext())
			.zoneId(zoneId())
			.root(node)
			.build();
	}

	// VisibleForTests
	@org.immutables.value.Value.Auxiliary
	public ASTNode abstractSyntaxTree(String expression) throws ParseException {
		return new ShuntingYardConverter(expression, tokens(expression), operators(), functions())
			.toAbstractSyntaxTree();
	}

	// VisibleForTests
	@org.immutables.value.Value.Auxiliary
	public List<Token> tokens(String expression) throws ParseException {
		return new Tokenizer(expression, operators()).parse();
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
				result = PrefixOperatorNode.of(token, operator(token, PrefixOperator.class), map(startNode.getParameters().get(0)));
				break;
			case POSTFIX_OPERATOR:
				result = PostfixOperatorNode.of(token, operator(token, PostfixOperator.class), map(startNode.getParameters().get(0)));
				break;
			case INFIX_OPERATOR:
				result = InfixOperatorNode.of(token, operator(token, InfixOperator.class), map(startNode.getParameters().get(0)), map(startNode.getParameters().get(1)));
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

	private <T extends Operator> T operator(Token token, Class<T> operatorType) {
		return operators().get(operatorType, token.value());
	}

	private Node getVariableOrConstant(Token token) {
		Value<?> result = constants().get(token.value());
		if (result!=null) {
			return AnyTypeValueNode.of(token, result);
		}
		return ValueLookup.of(token);
	}

	private FunctionNode evaluateFunction(ASTNode startNode, Token token) throws EvaluationException {
		Evaluateable function = functions().get(token.value());
		List<Node> parameterResults = new ArrayList<>();
		for (int i = 0; i < startNode.getParameters().size(); i++) {
			parameterResults.add(map(startNode.getParameters().get(i)));
		}

		return FunctionNode.of(token, function, parameterResults);
	}

	private ArrayAccessNode evaluateArrayIndex(ASTNode startNode) throws EvaluationException {
		return ArrayAccessNode.of(startNode.getToken(), map(startNode.getParameters().get(0)), map(startNode.getParameters().get(1)));
	}

	private StructureAccessNode evaluateStructureSeparator(ASTNode startNode) throws EvaluationException {
		Node structure = map(startNode.getParameters().get(0));
		Token nameToken = startNode.getParameters().get(1).getToken();
		return StructureAccessNode.of(startNode.getToken(), structure, nameToken);
	}

	private static Value.NumberValue numberOfString(String value, MathContext mathContext) {
		if (value.startsWith("0x") || value.startsWith("0X")) {
			BigInteger hexToInteger = new BigInteger(value.substring(2), 16);
			return Value.of(new BigDecimal(hexToInteger, mathContext));
		} else {
			return Value.of(new BigDecimal(value, mathContext));
		}
	}

	// TODO @Deprecated
	public static ImmutableExpressionFactory of(Configuration configuration) {
		return ImmutableExpressionFactory.builder()
			.constants(configuration.getConstantResolver())
			.zoneId(configuration.getDefaultZoneId())
			.mathContext(configuration.getMathContext())
			.functions(configuration.functions())
			.operators(configuration.getOperatorResolver())
			.build();
	}

	public static ImmutableExpressionFactory defaults() {
		return ImmutableExpressionFactory.builder()
			.constants(Defaults.constants())
			.zoneId(ZoneId.systemDefault())
			.mathContext(Defaults.mathContext())
			.functions(Defaults.functions())
			.operators(Defaults.operators())
			.build();
	}

}
