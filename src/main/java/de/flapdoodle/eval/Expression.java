/**
 * Copyright (C) 2023
 * Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.eval;

import de.flapdoodle.eval.config.Configuration;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.tree.*;
import de.flapdoodle.eval.operators.InfixOperator;
import de.flapdoodle.eval.operators.Operator;
import de.flapdoodle.eval.operators.PostfixOperator;
import de.flapdoodle.eval.operators.PrefixOperator;
import de.flapdoodle.eval.parser.*;
import de.flapdoodle.types.Either;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

@org.immutables.value.Value.Immutable
public abstract class Expression {
	protected abstract Configuration configuration();

	public abstract String raw();

	@org.immutables.value.Value.Default
	public ValueResolver constants() {
		return configuration().getConstantResolver();
	}

	@org.immutables.value.Value.Auxiliary
	public Expression withConstant(String variable, Value<?> value) {
		if (constants().get(variable) == null || configuration().isAllowOverwriteConstants()) {
			return ImmutableExpression.builder().from(this)
				.constants(ValueResolver.empty()                      
					.with(variable, value)
					.andThen(constants()))
				.build();
		} else {
			throw new UnsupportedOperationException(
				String.format("Can't set value for constant '%s'", variable));
		}
	}

	@org.immutables.value.Value.Default
	public MathContext mathContext() {
		return configuration().getMathContext();
	}

	@org.immutables.value.Value.Default
	public ZoneId zoneId() {
		return configuration().getDefaultZoneId();
	}

	@org.immutables.value.Value.Derived
	public Either<ASTNode, ParseException> getAbstractSyntaxTree() {
		Tokenizer tokenizer = new Tokenizer(raw(), configuration().getOperatorResolver());
		try {
			ShuntingYardConverter converter =
				new ShuntingYardConverter(raw(), tokenizer.parse(), configuration().getOperatorResolver(), configuration().functions());
			return Either.left(converter.toAbstractSyntaxTree());
		}
		catch (ParseException px) {
			return Either.right(px);
		}
	}

	@Deprecated
	@org.immutables.value.Value.Auxiliary
	// TODO mit @Check annotieren?
	public void validate() throws ParseException {
		Either<ASTNode, ParseException> astOrException = getAbstractSyntaxTree();
		
		if (!astOrException.isLeft()) throw astOrException.right();
	}

	@org.immutables.value.Value.Auxiliary
	public List<ASTNode> getAllASTNodes() throws ParseException {
		Either<ASTNode, ParseException> tree = getAbstractSyntaxTree();
		if (tree.isLeft())
			return getAllASTNodesForNode(tree.left());
		else throw tree.right();
	}

	@org.immutables.value.Value.Auxiliary
	public Set<String> getUsedVariables() throws ParseException {
		Set<String> variables = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		for (ASTNode node : getAllASTNodes()) {
			if (node.getToken().type() == TokenType.VARIABLE_OR_CONSTANT
				&& !constants().has(node.getToken().value())) {
				variables.add(node.getToken().value());
			}
		}

		return variables;
	}

	@org.immutables.value.Value.Auxiliary
	public Set<String> getUndefinedVariables(ValueResolver variableResolver) throws ParseException {
		Set<String> variables = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (String variable : getUsedVariables()) {
			if (variableResolver.get(variable) == null) {
				variables.add(variable);
			}
		}
		return variables;
	}

	/**
	 * Evaluates the expression by parsing it (if not done before) and the evaluating it.
	 *
	 * @return The evaluation result value.
	 * @throws EvaluationException If there were problems while evaluating the expression.
	 * @throws ParseException      If there were problems while parsing the expression.
	 */
	public Value<?> evaluate(ValueResolver variableResolver) throws EvaluationException, ParseException {
		// TODO das muss aufgelöst werden
		if (ThreadLocalRandom.current().nextBoolean()) {
			return ExpressionFactory.of(configuration())
				.withConstants(constants())
				.withMathContext(mathContext())
				.withZoneId(zoneId())
				.parse(raw()).evaluate(variableResolver);
		}
		Either<ASTNode, ParseException> ast = getAbstractSyntaxTree();
		if (ast.isLeft()) {
			Node start = map(ast.left());
			return start.evaluate(variableResolver, context());
		} else throw ast.right();
	}

	@org.immutables.value.Value.Auxiliary
	protected Node map(ASTNode startNode) throws EvaluationException {
		Node result;
		Token token = startNode.getToken();
		switch (token.type()) {
			case NUMBER_LITERAL:
				result = ComparableValueNode.of(token, numberOfString(token.value(), configuration().getMathContext()));
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
		return configuration().getOperatorResolver().get(operatorType, token.value());
	}

	// VisibleInTest
	private EvaluationContext context() {
		return EvaluationContext.builder()
			.mathContext(mathContext())
			.zoneId(zoneId())
			.build();
	}

	private Node getVariableOrConstant(Token token) {
		Value<?> result = constants().get(token.value());
		if (result!=null) {
			return AnyTypeValueNode.of(token, result);
		}
		return ValueLookup.of(token);
	}

	private FunctionNode evaluateFunction(ASTNode startNode, Token token) throws EvaluationException {
		Evaluateable function = configuration().functions().get(token.value());
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

	private static List<ASTNode> getAllASTNodesForNode(ASTNode node) {
		List<ASTNode> nodes = new ArrayList<>();
		nodes.add(node);
		for (ASTNode child : node.getParameters()) {
			nodes.addAll(getAllASTNodesForNode(child));
		}
		return nodes;
	}

	private static Value.NumberValue numberOfString(String value, MathContext mathContext) {
		if (value.startsWith("0x") || value.startsWith("0X")) {
			BigInteger hexToInteger = new BigInteger(value.substring(2), 16);
			return Value.of(new BigDecimal(hexToInteger, mathContext));
		} else {
			return Value.of(new BigDecimal(value, mathContext));
		}
	}

	public static Expression of(String expressionString) {
		return ImmutableExpression.builder()
			.raw(expressionString)
			.configuration(Configuration.defaultConfiguration())
			.build();
	}

	public static Expression of(String expressionString, Configuration configuration) {
		return ImmutableExpression.builder()
			.raw(expressionString)
			.configuration(configuration)
			.build();
	}
}
