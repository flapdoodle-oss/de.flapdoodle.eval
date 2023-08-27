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

import de.flapdoodle.eval.operators.Operator;
import de.flapdoodle.eval.operators.arithmetic.*;
import de.flapdoodle.eval.operators.booleans.*;

public interface OperatorResolver {
	default <T extends Operator> boolean hasOperator(Class<T> type, String operatorString) {
		return get(type, operatorString) != null;
	}

	<T extends Operator> T get(Class<T> type, String operatorString);

	default OperatorResolver andThen(OperatorResolver fallback) {
		OperatorResolver that = this;

		return new OperatorResolver() {
			@Override
			public <T extends Operator> T get(Class<T> type, String operatorString) {
				T operator = that.get(type, operatorString);
				if (operator == null) {
					return fallback.get(type, operatorString);
				}
				return operator;
			}
		};
	}

	static OperatorResolver defaults() {
		return MapBasedOperatorResolver.builder()
			// arithmetic
			.putPrefixOperators("+", new PrefixPlus())
			.putPrefixOperators("-", new PrefixMinus())
			.putInfixOperators("+", new Plus())
			.putInfixOperators("-", new Minus())
			.putInfixOperators("*", new Multiply())
			.putInfixOperators("/", new Divide())
			.putInfixOperators("^", new PowerOf())
			.putInfixOperators("%", new Modulo())
			// booleans
			.putInfixOperators("=", new Equals())
			.putInfixOperators("==", new Equals())
			.putInfixOperators("===", new Same())
			.putInfixOperators("!=", new NotEquals())
			.putInfixOperators("<>", new NotEquals())
			.putInfixOperators(">", new Greater())
			.putInfixOperators(">=", new GreaterOrEquals())
			.putInfixOperators("<", new Less())
			.putInfixOperators("<=", new LessOrEquals())
			.putInfixOperators("&&", new And())
			.putInfixOperators("||", new Or())
			.putPrefixOperators("!", new PrefixNot())
			.build();
	}
}
