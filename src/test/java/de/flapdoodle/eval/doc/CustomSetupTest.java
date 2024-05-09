/*
 * Copyright (C) 2023
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.eval.doc;

import de.flapdoodle.eval.core.ExpressionFactory;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.*;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.exceptions.ParseException;
import de.flapdoodle.eval.core.tree.EvalFailedWithException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomSetupTest {

	@Test
	public void example() throws ParseException, EvaluationException {
		ExpressionFactory expressionFactory = ExpressionFactory.builder()
			.constants(VariableResolver.empty())
			.evaluatables(TypedEvaluableMap.builder()
				.putMap("add", new Plus())
				.putMap("minus", new Minus())
				.build())
			.operatorMap(OperatorMap.builder()
				.putPrefix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "add"))
				.putPrefix("-", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "minus"))

				.putInfix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "add"))
				.putInfix("-", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "minus"))
				.build())
			.arrayAccess(new ArrayAccess())
			.propertyAccess(new PropertyAccess())
			.numberAsValue((s, m) -> Integer.parseInt(s))
			.stringAsValue(s -> s)
			.exceptionMapper(EvalFailedWithException.mapper())
			.build();

		assertThat(expressionFactory.parse("add(2,3)").evaluate(VariableResolver.empty()))
			.isEqualTo(5);
		assertThat(expressionFactory.parse("2+3").evaluate(VariableResolver.empty()))
			.isEqualTo(5);
		assertThat(expressionFactory.parse("2-3").evaluate(VariableResolver.empty()))
			.isEqualTo(-1);
		assertThat(expressionFactory.parse("-2").evaluate(VariableResolver.empty()))
			.isEqualTo(-2);

		assertThat(expressionFactory.parse("add(a,b)").evaluate(VariableResolver.empty()
			.with("a", Evaluated.ofNull(Integer.class))
			.and("b", Evaluated.ofNull(Integer.class))))
			.isNull();

		assertThat(expressionFactory.parse("add(a,b)").evaluate(VariableResolver.empty()
			.with("a", Evaluated.ofNullable(Integer.class, 1))
			.and("b", Evaluated.ofNull(Integer.class))))
			.isEqualTo(1);

		assertThat(expressionFactory.parse("add(a,b)").evaluate(VariableResolver.empty()
			.with("a", Evaluated.ofNull(Integer.class))
			.and("b", Evaluated.ofNullable(Integer.class, 2))))
			.isEqualTo(2);
	}
}
