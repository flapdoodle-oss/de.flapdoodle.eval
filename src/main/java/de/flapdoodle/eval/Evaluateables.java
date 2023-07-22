package de.flapdoodle.eval;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.util.List;

public abstract class Evaluateables {
	public abstract static class Base implements Evaluateable {
		private final ImmutableParameters parameters;

		protected Base(Parameter<?>... parameters) {
			this.parameters = Parameters.of(parameters);
		}

		@Override
		public final Parameters parameters() {
			return parameters;
		}

		@Override
		public final Value<?> evaluate(ValueResolver valueResolver, Expression expression, Token token, List<Value<?>> arguments) throws EvaluationException {
			parameters().validate(token, arguments);
			return evaluateValidated(valueResolver, expression, token, arguments);
		}

		protected abstract Value<?> evaluateValidated(ValueResolver valueResolver, Expression expression, Token token, List<Value<?>> parameters)
			throws EvaluationException;
	}

	public abstract static class Single<T extends Value<?>> extends Base {

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
		protected final Value<?> evaluateValidated(ValueResolver valueResolver, Expression expression, Token token, List<Value<?>> arguments) throws EvaluationException {
			return evaluate(valueResolver, expression, token, definition.type().cast(arguments.get(0)));
		}

		public abstract Value<?> evaluate(ValueResolver variableResolver, Expression expression, Token functionToken, T parameterValue)
			throws EvaluationException;
	}
}
