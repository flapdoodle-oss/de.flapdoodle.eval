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

import de.flapdoodle.eval.config.TestConfigurationProvider;
import de.flapdoodle.eval.core.Expression;
import de.flapdoodle.eval.core.ExpressionFactory;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.ParseException;
import de.flapdoodle.eval.values.Value;

import java.math.BigDecimal;

public abstract class BaseExpressionEvaluatorTest {

	final ExpressionFactory factory =
			TestConfigurationProvider.StandardFactoryWithAdditionalTestOperators;

	protected String evaluate(String expressionString) throws ParseException, EvaluationException {
		Expression expression = createExpression(expressionString);
		return expression.evaluate(VariableResolver.empty()).toString();
	}

	protected String evaluate(String expressionString, VariableResolver variableResolver) throws ParseException, EvaluationException {
		Expression expression = createExpression(expressionString);
		return expression.evaluate(variableResolver).toString();
	}

	Expression createExpression(String expressionString) throws ParseException, EvaluationException {
		return factory.parse(expressionString);
	}

	protected static Value.NumberValue numberValueOf(String doubleAsString) {
		return Value.of(new BigDecimal(doubleAsString));
	}
}
