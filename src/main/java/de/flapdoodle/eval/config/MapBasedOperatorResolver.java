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

import de.flapdoodle.eval.operators.InfixOperator;
import de.flapdoodle.eval.operators.Operator;
import de.flapdoodle.eval.operators.PostfixOperator;
import de.flapdoodle.eval.operators.PrefixOperator;
import de.flapdoodle.types.Pair;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Value.Immutable
public abstract class MapBasedOperatorResolver implements OperatorResolver, HasOperator {

	protected abstract Map<String, InfixOperator> infixOperators();

	protected abstract Map<String, PrefixOperator> prefixOperators();

	protected abstract Map<String, PostfixOperator> postfixOperators();

	@Override
	public <T extends Operator> T get(Class<T> type, String operatorString) {
		if (type.isAssignableFrom(InfixOperator.class)) {
			return type.cast(infixOperators().get(operatorString));
		}
		if (type.isAssignableFrom(PrefixOperator.class)) {
			return type.cast(prefixOperators().get(operatorString));
		}
		if (type.isAssignableFrom(PostfixOperator.class)) {
			return type.cast(postfixOperators().get(operatorString));
		}
		throw new IllegalArgumentException("operator type unknown: " + type + "(" + operatorString + ")");
	}

	@Override
	public boolean hasStartingWith(Class<? extends Operator> type, String value) {
		Set<String> keys = Collections.emptySet();
		if (type.isAssignableFrom(InfixOperator.class)) {
			keys=infixOperators().keySet();
		}
		if (type.isAssignableFrom(PrefixOperator.class)) {
			keys=postfixOperators().keySet();
		}
		if (type.isAssignableFrom(PostfixOperator.class)) {
			keys=postfixOperators().keySet();
		}
		return keys.stream().anyMatch(it -> it.startsWith(value));
	}

	public static ImmutableMapBasedOperatorResolver.Builder builder() {
		return ImmutableMapBasedOperatorResolver.builder();
	}

	public static ImmutableMapBasedOperatorResolver of(Pair<String, Operator>... operators) {
		ImmutableMapBasedOperatorResolver.Builder builder = ImmutableMapBasedOperatorResolver.builder();
		for (Pair<String, Operator> entry : operators) {
			Operator value = entry.second();
			boolean foundType = false;
			if (value instanceof InfixOperator) {
				builder.putInfixOperators(entry.first(), (InfixOperator) value);
				foundType = true;
			}
			if (value instanceof PrefixOperator) {
				builder.putPrefixOperators(entry.first(), (PrefixOperator) value);
				foundType = true;
			}
			if (value instanceof PostfixOperator) {
				builder.putPostfixOperators(entry.first(), (PostfixOperator) value);
				foundType = true;
			}
			if (!foundType) {
				throw new IllegalArgumentException("unknown type: " + value);
			}
		}
		return builder.build();
	}
}
