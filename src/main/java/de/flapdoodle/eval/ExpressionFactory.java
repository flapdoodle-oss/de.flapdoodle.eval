package de.flapdoodle.eval;

import de.flapdoodle.eval.config.*;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.evaluate.*;
import de.flapdoodle.eval.operators.InfixOperator;
import de.flapdoodle.eval.operators.Operator;
import de.flapdoodle.eval.operators.PostfixOperator;
import de.flapdoodle.eval.operators.PrefixOperator;
import de.flapdoodle.eval.parser.*;

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

	@org.immutables.value.Value.Auxiliary
	public ImmutableParsedExpression parse(String expression) throws ParseException, EvaluationException {
		Tokenizer tokenizer = new Tokenizer(expression, operators());
		ShuntingYardConverter converter = new ShuntingYardConverter(expression, tokenizer.parse(), operators(), functions());
		Node node = map(converter.toAbstractSyntaxTree());
		return ParsedExpression.builder()
			.mathContext(mathContext())
			.zoneId(zoneId())
			.root(node)
			.build();
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
