package de.flapdoodle.eval;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Evaluateables {
	public abstract static class Base implements Evaluateable {
		private final Parameters parameters;

		protected Base(Parameters parameters) {
			this.parameters = parameters;
		}

		@Override
		public final Parameters parameters() {
			return parameters;
		}

		@Override
		public final Value<?> evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, List<Value<?>> arguments) throws EvaluationException {
			parameters().validate(token, arguments);
			return evaluateValidated(valueResolver, evaluationContext, token, arguments);
		}

		protected abstract Value<?> evaluateValidated(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, List<Value<?>> parameters)
			throws EvaluationException;
	}

	/**
	 * Single Argument
	 *
	 * @param <T>
	 */
	public abstract static class Single<T extends Value<?>> extends Base {

		private final Parameter<T> definition;

		protected Single(Parameter<T> definition) {
			super(Parameters.of(definition));
			this.definition = definition;
		}

		protected Single(Class<T> parameterType, String name) {
			this(Parameter.of(parameterType, name));
		}

		protected Single(Class<T> parameterType) {
			this(parameterType, "value");
		}

		@Override
		protected final Value<?> evaluateValidated(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, List<Value<?>> arguments)
			throws EvaluationException {
			return evaluate(valueResolver, evaluationContext, token, definition.type().cast(arguments.get(0)));
		}

		protected abstract Value<?> evaluate(ValueResolver variableResolver, EvaluationContext evaluationContext, Token functionToken, T parameterValue)
			throws EvaluationException;
	}

	/**
	 * Single VarArg
	 *
	 * @param <T>
	 */
	public abstract static class SingleVararg<T extends Value<?>> extends Base {

		private final Parameter<T> definition;

		protected SingleVararg(Parameter<T> definition) {
			super(Parameters.varArgWith(definition));

			this.definition = definition;
		}

		protected SingleVararg(Class<T> parameterType, String name) {
			this(Parameter.of(parameterType, name));
		}

		protected SingleVararg(Class<T> parameterType) {
			this(parameterType, "value");
		}

		@Override
		protected final Value<?> evaluateValidated(ValueResolver variableResolver, EvaluationContext evaluationContext, Token functionToken, List<Value<?>> arguments)
			throws EvaluationException {
			return evaluateVarArg(variableResolver, evaluationContext, functionToken, arguments.stream().map(it -> definition.type().cast(it)).collect(
				Collectors.toList()));
		}

		protected abstract Value<?> evaluateVarArg(ValueResolver variableResolver, EvaluationContext evaluationContext, Token functionToken, List<T> parameterValues);
	}

	/**
	 * Two Arguments
	 */
	public abstract static class Tuple<A extends Value<?>, B extends Value<?>> extends Base {

		private final Parameter<A> a;
		private final Parameter<B> b;

		protected Tuple(Parameter<A> a, Parameter<B> b) {
			super(Parameters.of(a, b));
			this.a = a;
			this.b = b;
		}

		@Override
		protected final Value<?> evaluateValidated(ValueResolver variableResolver, EvaluationContext evaluationContext, Token functionToken, List<Value<?>> arguments)
			throws EvaluationException {
			if (arguments.size() != 2) throw EvaluationException.ofUnsupportedDataTypeInOperation(functionToken);
			return evaluate(variableResolver, evaluationContext, functionToken,
				a.type().cast(arguments.get(0)),
				b.type().cast(arguments.get(1))
			);
		}

		protected abstract Value<?> evaluate(ValueResolver variableResolver, EvaluationContext evaluationContext, Token functionToken, A a, B b)
			throws EvaluationException;
	}

	/**
	 * Three Arguments
	 */
	public abstract static class Triple<A extends Value<?>, B extends Value<?>, C extends Value<?>> extends Base {

		private final Parameter<A> a;
		private final Parameter<B> b;
		private final Parameter<C> c;

		protected Triple(Parameter<A> a, Parameter<B> b, Parameter<C> c) {
			super(Parameters.of(a, b, c));
			this.a = a;
			this.b = b;
			this.c = c;
		}

		@Override
		protected final Value<?> evaluateValidated(ValueResolver variableResolver, EvaluationContext evaluationContext, Token functionToken, List<Value<?>> arguments)
			throws EvaluationException {
			if (arguments.size() != 3) throw EvaluationException.ofUnsupportedDataTypeInOperation(functionToken);
			return evaluate(variableResolver, evaluationContext, functionToken,
				a.type().cast(arguments.get(0)),
				b.type().cast(arguments.get(1)),
				c.type().cast(arguments.get(2))
			);
		}

		protected abstract Value<?> evaluate(ValueResolver variableResolver, EvaluationContext evaluationContext, Token functionToken, A a, B b, C c)
			throws EvaluationException;
	}

}
