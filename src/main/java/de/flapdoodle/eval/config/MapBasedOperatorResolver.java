/**
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
package de.flapdoodle.eval.config;

import de.flapdoodle.eval.operators.AbstractInfixOperator;
import de.flapdoodle.eval.operators.AbstractPostfixOperator;
import de.flapdoodle.eval.operators.AbstractPrefixOperator;
import de.flapdoodle.eval.operators.Operator;
import de.flapdoodle.types.Pair;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
public abstract class MapBasedOperatorResolver implements OperatorResolver {

	protected abstract Map<String, AbstractInfixOperator> infixOperators();
	protected abstract Map<String, AbstractPrefixOperator> prefixOperators();
	protected abstract Map<String, AbstractPostfixOperator> postfixOperators();

	@Override
	public <T extends Operator> T getOperator(Class<T> type, String operatorString) {
		if (type.isAssignableFrom(AbstractInfixOperator.class)) {
			return type.cast(infixOperators().get(operatorString));
		}
		if (type.isAssignableFrom(AbstractPrefixOperator.class)) {
			return type.cast(prefixOperators().get(operatorString));
		}
		if (type.isAssignableFrom(AbstractPostfixOperator.class)) {
			return type.cast(postfixOperators().get(operatorString));
		}
		throw new IllegalArgumentException("operator type unknown: "+type+"("+operatorString+")");
	}

	public static ImmutableMapBasedOperatorResolver.Builder builder() {
		return ImmutableMapBasedOperatorResolver.builder();
	}

	public static ImmutableMapBasedOperatorResolver of(Pair<String, Operator>... operators) {
		ImmutableMapBasedOperatorResolver.Builder builder = ImmutableMapBasedOperatorResolver.builder();
		for (Pair<String, Operator> entry : operators) {
			Operator value = entry.second();
			boolean foundType=false;
			if (value instanceof AbstractInfixOperator) {
				builder.putInfixOperators(entry.first(), (AbstractInfixOperator) value);
				foundType=true;
			}
			if (value instanceof AbstractPrefixOperator) {
				builder.putPrefixOperators(entry.first(), (AbstractPrefixOperator) value);
				foundType=true;
			}
			if (value instanceof AbstractPostfixOperator) {
				builder.putPostfixOperators(entry.first(), (AbstractPostfixOperator) value);
				foundType=true;
			}
			if (!foundType) {
				throw new IllegalArgumentException("unknown type: "+value);
			}
		}
		return builder.build();
	}
}
