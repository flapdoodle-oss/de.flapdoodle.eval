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
package de.flapdoodle.eval.config;

import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.functions.AbstractFunction;
import de.flapdoodle.eval.functions.Function;
import de.flapdoodle.eval.Parameter;
import de.flapdoodle.eval.operators.AbstractPostfixOperator;
import de.flapdoodle.eval.operators.AbstractPrefixOperator;
import de.flapdoodle.eval.operators.Precedence;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.types.Pair;

import java.math.BigDecimal;
import java.util.List;

public class TestConfigurationProvider {

	public static final ImmutableConfiguration StandardConfigurationWithAdditionalTestOperators;

	static {
		Configuration configuration = Configuration.defaultConfiguration()
			.withOperators(
				Pair.of("++", new PrefixPlusPlusOperator()),
				Pair.of("++", new PostfixPlusPlusOperator()),
				Pair.of("?", new PostfixQuestionOperator())
				);
		Pair<String, Function>[] functions = new Pair[] { Pair.of("TEST", new DummyFunction()) };
		StandardConfigurationWithAdditionalTestOperators = ImmutableConfiguration.copyOf(configuration)
			.withFunctionResolver(MapBasedFunctionResolver.of(functions)
				.andThen(configuration.getFunctionResolver()));
	}

	public static class DummyFunction extends AbstractFunction.SingleVararg<Value.StringValue> {

		public DummyFunction() {
			super(Parameter.varArgWith(Value.StringValue.class, "input"));
		}

		@Override public Value<?> evaluateVarArg(ValueResolver variableResolver, Expression expression, Token functionToken,
			List<Value.StringValue> parameterValues) {
			// dummy implementation
			return Value.of("OK");
		}
	}

	public static class PrefixPlusPlusOperator extends AbstractPrefixOperator.Typed<Value.NumberValue> {

		public PrefixPlusPlusOperator() {
			super(Precedence.OPERATOR_PRECEDENCE_UNARY, false, Value.NumberValue.class);
		}

		@Override protected Value<?> evaluateTyped(Expression expression, Token operatorToken, Value.NumberValue operand) throws EvaluationException {
			return Value.of(operand.wrapped().add(BigDecimal.ONE));
		}
	}

	public static class PostfixPlusPlusOperator extends AbstractPostfixOperator.Typed<Value.NumberValue> {

		protected PostfixPlusPlusOperator() {
			super(Value.NumberValue.class);
		}

		@Override protected Value<?> evaluateTyped(Expression expression, Token operatorToken, Value.NumberValue operand) throws EvaluationException {
			return Value.of(operand.wrapped().add(BigDecimal.ONE));
		}
	}

	public static class PostfixQuestionOperator extends AbstractPostfixOperator.Typed<Value.NullValue> {

		public PostfixQuestionOperator() {
			super(Precedence.OPERATOR_PRECEDENCE_UNARY, false, Value.NullValue.class);
		}

		@Override protected Value<?> evaluateTyped(Expression expression, Token operatorToken, Value.NullValue operand) throws EvaluationException {
			return Value.of("?");
		}
	}
}
