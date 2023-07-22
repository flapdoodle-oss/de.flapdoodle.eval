package de.flapdoodle.eval;

import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.util.List;

public abstract class Evaluateables {
	public abstract static class Base implements Evaluateable {
		private final ImmutableParameters parameters;

		protected Base(Parameter<?> parameter, Parameter<?>... parameters) {
			this.parameters = Parameters.of(parameter, parameters);
		}

		@Override
		public Parameters parameters() {
			return parameters;
		}
	}

	public abstract static class Single<T extends Value<?>> implements Evaluateable {

		private final Parameter<T> definition;

		protected Single(Parameter<T> definition) {
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
		public final Value<?> evaluate(ValueResolver variableResolver, Expression expression, Token functionToken, List<Value<?>> parameterValues)
			throws EvaluationException {
			if (parameterValues.size()!=1) throw EvaluationException.ofUnsupportedDataTypeInOperation(functionToken);
			return evaluate(variableResolver, expression, functionToken, definition.type().cast(parameterValues.get(0)));
		}

		public abstract Value<?> evaluate(ValueResolver variableResolver, Expression expression, Token functionToken, T parameterValue)
			throws EvaluationException;
	}
}
