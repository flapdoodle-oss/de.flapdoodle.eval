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
import de.flapdoodle.eval.operators.InfixOperator;
import de.flapdoodle.eval.operators.PostfixOperator;
import de.flapdoodle.eval.operators.PrefixOperator;
import de.flapdoodle.eval.parser.*;
import de.flapdoodle.types.Either;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@org.immutables.value.Value.Immutable
public abstract class Expression {
	public abstract Configuration configuration();

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

	@org.immutables.value.Value.Derived
	public Either<ASTNode, ParseException> getAbstractSyntaxTree() {
		Tokenizer tokenizer = new Tokenizer(raw(), configuration());
		try {
			ShuntingYardConverter converter =
				new ShuntingYardConverter(raw(), tokenizer.parse(), configuration());
			return Either.left(converter.toAbstractSyntaxTree());
		}
		catch (ParseException px) {
			return Either.right(px);
		}
	}

	@Deprecated
	@org.immutables.value.Value.Auxiliary
	public void validate() throws ParseException {
		if (!getAbstractSyntaxTree().isLeft()) throw getAbstractSyntaxTree().right();
	}

	public List<ASTNode> getAllASTNodes() throws ParseException {
		Either<ASTNode, ParseException> tree = getAbstractSyntaxTree();
		if (tree.isLeft())
			return getAllASTNodesForNode(tree.left());
		else throw tree.right();
	}

	private static List<ASTNode> getAllASTNodesForNode(ASTNode node) {
		List<ASTNode> nodes = new ArrayList<>();
		nodes.add(node);
		for (ASTNode child : node.getParameters()) {
			nodes.addAll(getAllASTNodesForNode(child));
		}
		return nodes;
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

	@org.immutables.value.Value.Auxiliary
	public ASTNode createExpressionNode(String expression) throws ParseException {
		Tokenizer tokenizer = new Tokenizer(expression, configuration());
		ShuntingYardConverter converter =
			new ShuntingYardConverter(expression, tokenizer.parse(), configuration());
		return converter.toAbstractSyntaxTree();
	}

	/**
	 * Evaluates the expression by parsing it (if not done before) and the evaluating it.
	 *
	 * @return The evaluation result value.
	 * @throws EvaluationException If there were problems while evaluating the expression.
	 * @throws ParseException      If there were problems while parsing the expression.
	 */
	public Value<?> evaluate(ValueResolver variableResolver) throws EvaluationException, ParseException {
		Either<ASTNode, ParseException> ast = getAbstractSyntaxTree();
		if (ast.isLeft()) {
			return evaluateSubtree(variableResolver, ast.left());
		} else throw ast.right();
	}

	/**
	 * Evaluates only a subtree of the abstract syntax tree.
	 *
	 * @param startNode The {@link ASTNode} to start evaluation from.
	 * @return The evaluation result value.
	 * @throws EvaluationException If there were problems while evaluating the expression.
	 */
	public Value<?> evaluateSubtree(ValueResolver variableResolver, ASTNode startNode) throws EvaluationException {
		Token token = startNode.getToken();
		Value<?> result;
		switch (token.type()) {
			case NUMBER_LITERAL:
				result = numberOfString(token.value(), configuration().getMathContext());
				break;
			case STRING_LITERAL:
				result = Value.of(token.value());
				break;
			case VARIABLE_OR_CONSTANT:
				result = getVariableOrConstant(variableResolver, token);
				if (result instanceof Value.ExpressionValue) {
					result = evaluateSubtree(variableResolver, ((Value.ExpressionValue) result).wrapped());
				}
				break;
			case PREFIX_OPERATOR:
				result =
					token
						.operator(PrefixOperator.class)
						.evaluate(this, token, evaluateSubtree(variableResolver, startNode.getParameters().get(0)));
				break;
			case POSTFIX_OPERATOR:
				result =
					token
						.operator(PostfixOperator.class)
						.evaluate(this, token, evaluateSubtree(variableResolver, startNode.getParameters().get(0)));
				break;
			case INFIX_OPERATOR:
				result =
					token
						.operator(InfixOperator.class)
						.evaluate(
							this,
							token,
							evaluateSubtree(variableResolver, startNode.getParameters().get(0)),
							evaluateSubtree(variableResolver, startNode.getParameters().get(1)));
				break;
			case ARRAY_INDEX:
				result = evaluateArrayIndex(variableResolver, startNode);
				break;
			case STRUCTURE_SEPARATOR:
				result = evaluateStructureSeparator(variableResolver, startNode);
				break;
			case FUNCTION:
				result = evaluateFunction(variableResolver, startNode, token);
				break;
			default:
				throw new EvaluationException(token, "Unexpected evaluation token: " + token);
		}

//		return result.isNumberValue() ? roundAndStripZerosIfNeeded(result) : result;
		return result;
	}

	private Value<?> getVariableOrConstant(ValueResolver variableResolver, Token token) throws EvaluationException {
		Value<?> result = constants().get(token.value());
		if (result == null) {
			result = variableResolver.get(token.value());
		}
		if (result == null) {
			throw new EvaluationException(
				token, String.format("Variable or constant value for '%s' not found", token.value()));
		}
		return result;
	}

	private Value<?> evaluateFunction(ValueResolver variableResolver, ASTNode startNode, Token token)
		throws EvaluationException {
		List<Value<?>> parameterResults = new ArrayList<>();
		for (int i = 0; i < startNode.getParameters().size(); i++) {
			if (token.function().parameterIsLazy(i)) {
				parameterResults.add(Value.of(startNode.getParameters().get(i)));
			} else {
				parameterResults.add(evaluateSubtree(variableResolver, startNode.getParameters().get(i)));
			}
		}

		Evaluateable function = token.function();

//    function.validatePreEvaluation(token, parameterResults);

		return function.evaluate(variableResolver, this, token, parameterResults);
	}

	private Value<?> evaluateArrayIndex(ValueResolver variableResolver, ASTNode startNode) throws EvaluationException {
		Value<?> array = evaluateSubtree(variableResolver, startNode.getParameters().get(0));
		Value<?> index = evaluateSubtree(variableResolver, startNode.getParameters().get(1));

		if (array instanceof Value.ArrayValue && index instanceof Value.NumberValue) {
			return ((Value.ArrayValue) array).wrapped().get(((Value.NumberValue) index).wrapped().intValue());
		} else {
			throw EvaluationException.ofUnsupportedDataTypeInOperation(startNode.getToken());
		}
	}

	private Value<?> evaluateStructureSeparator(ValueResolver variableResolver, ASTNode startNode) throws EvaluationException {
		Value<?> structure = evaluateSubtree(variableResolver, startNode.getParameters().get(0));
		Token nameToken = startNode.getParameters().get(1).getToken();
		String name = nameToken.value();

		if (structure instanceof Value.MapValue) {
			Value.MapValue structure1 = (Value.MapValue) structure;
			if (!structure1.wrapped().containsKey(name)) {
				throw new EvaluationException(
					nameToken, String.format("Field '%s' not found in structure", name));
			}
			return structure1.wrapped().get(name);
		} else {
			throw EvaluationException.ofUnsupportedDataTypeInOperation(startNode.getToken());
		}
	}

	/**
	 * moved from somewhere
	 */

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
