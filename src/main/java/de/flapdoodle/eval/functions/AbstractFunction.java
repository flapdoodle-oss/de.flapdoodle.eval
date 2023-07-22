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
package de.flapdoodle.eval.functions;

import de.flapdoodle.eval.*;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractFunction implements Function {
	private final ImmutableParameters parameters;

	protected AbstractFunction(Parameter<?> definition, Parameter<?>... definitions) {
		this.parameters = Parameters.of(definition, definitions);
	}

	@Override
	public Parameters parameters() {
		return parameters;
	}

	@Override
	public final Value<?> evaluate(ValueResolver valueResolver, Expression expression, Token token, List<Value<?>> arguments) throws EvaluationException {
		parameters().validate(token, arguments);
		return evaluateValidated(valueResolver, expression, token, arguments);
	}

	protected abstract Value<?> evaluateValidated(ValueResolver valueResolver, Expression expression, Token token, List<Value<?>> parameters)
		throws EvaluationException;

	public abstract static class Single<T extends Value<?>> extends AbstractFunction {

		private final Parameter<T> definition;

		protected Single(Parameter<T> definition) {
			super(definition);
			if (definition.isVarArg()) throw new IllegalArgumentException("varArg is true");
			this.definition = definition;
		}

		protected Single(Class<T> parameterType, String name) {
			this(Parameter.of(parameterType, name));
		}

		protected Single(Class<T> parameterType) {
			this(parameterType, "value");
		}

		@Override
		public final Value<?> evaluateValidated(ValueResolver variableResolver, Expression expression, Token functionToken, List<Value<?>> arguments)
			throws EvaluationException {
			if (arguments.size()!=1) throw EvaluationException.ofUnsupportedDataTypeInOperation(functionToken);
			return evaluate(variableResolver, expression, functionToken, definition.type().cast(arguments.get(0)));
		}

		public abstract Value<?> evaluate(ValueResolver variableResolver, Expression expression, Token functionToken, T parameterValue)
			throws EvaluationException;
	}

	public abstract static class SingleVararg<T extends Value<?>> extends AbstractFunction {

		private final Parameter<T> definition;

		protected SingleVararg(Parameter<T> definition) {
			super(definition);
			if (!definition.isVarArg()) throw new IllegalArgumentException("varArg is false");
			this.definition = definition;
		}

		protected SingleVararg(Class<T> parameterType, String name) {
			this(Parameter.of(parameterType, name));
		}

		protected SingleVararg(Class<T> parameterType) {
			this(parameterType, "value");
		}

		@Override
		public final Value<?> evaluateValidated(ValueResolver variableResolver, Expression expression, Token functionToken, List<Value<?>> arguments)
			throws EvaluationException {
			return evaluateVarArg(variableResolver, expression, functionToken, arguments.stream().map(it -> definition.type().cast(it)).collect(
				Collectors.toList()));
		}

		public abstract Value<?> evaluateVarArg(ValueResolver variableResolver, Expression expression, Token functionToken, List<T> parameterValues);
	}

	public abstract static class Tuple<A extends Value<?>, B extends Value<?>> extends AbstractFunction {

		private final Parameter<A> a;
		private final Parameter<B> b;

		protected Tuple(Parameter<A> a, Parameter<B> b) {
			super(a, b);
			if (a.isVarArg()) throw new IllegalArgumentException("a.varArg is true");
			if (b.isVarArg()) throw new IllegalArgumentException("b.varArg is true");
			this.a = a;
			this.b = b;
		}

		@Override
		public final Value<?> evaluateValidated(ValueResolver variableResolver, Expression expression, Token functionToken, List<Value<?>> arguments)
			throws EvaluationException {
			if (arguments.size()!=2) throw EvaluationException.ofUnsupportedDataTypeInOperation(functionToken);
			return evaluate(variableResolver, expression, functionToken,
				a.type().cast(arguments.get(0)),
				b.type().cast(arguments.get(1))
			);
		}

		public abstract Value<?> evaluate(ValueResolver variableResolver, Expression expression, Token functionToken, A a, B b)
			throws EvaluationException;
	}

	public abstract static class Triple<A extends Value<?>, B extends Value<?>, C extends Value<?>> extends AbstractFunction {

		private final Parameter<A> a;
		private final Parameter<B> b;
		private final Parameter<C> c;

		protected Triple(Parameter<A> a, Parameter<B> b, Parameter<C> c) {
			super(a, b, c);
			if (a.isVarArg()) throw new IllegalArgumentException("a.varArg is true");
			if (b.isVarArg()) throw new IllegalArgumentException("b.varArg is true");
			if (c.isVarArg()) throw new IllegalArgumentException("c.varArg is true");
			this.a = a;
			this.b = b;
			this.c = c;
		}

		@Override
		public final Value<?> evaluateValidated(ValueResolver variableResolver, Expression expression, Token functionToken, List<Value<?>> arguments)
			throws EvaluationException {
			if (arguments.size()!=3) throw EvaluationException.ofUnsupportedDataTypeInOperation(functionToken);
			return evaluate(variableResolver, expression, functionToken,
				a.type().cast(arguments.get(0)),
				b.type().cast(arguments.get(1)),
				c.type().cast(arguments.get(2))
			);
		}

		public abstract Value<?> evaluate(ValueResolver variableResolver, Expression expression, Token functionToken, A a, B b, C c)
			throws EvaluationException;
	}
}
