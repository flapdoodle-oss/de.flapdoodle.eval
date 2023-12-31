package de.flapdoodle.eval.core.evaluables;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.exceptions.EvaluableException;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;

import java.util.List;
import java.util.Optional;

class TypedEvaluableAdapter<T> implements TypedEvaluable<T> {
	private final Signature<T> signature;
	private final Evaluable<T> delegate;

	public TypedEvaluableAdapter(Signature<T> signature, Evaluable<T> delegate) {
		this.signature = signature;
		this.delegate = delegate;
	}

	public Signature<T> signature() {
		return signature;
	}

	@Override
	public T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, List<?> arguments)
		throws EvaluationException {
		checkArguments(token, arguments);
		return delegate.evaluate(variableResolver, evaluationContext, token, arguments);
	}

	protected void checkArguments(Token token, List<?> arguments) throws EvaluationException {
		Optional<EvaluableException> error = signature().validateArguments(arguments);
		if (error.isPresent()) {
			throw new EvaluationException(token, error.get());
		}
	}

	@Override
	public String toString() {
		return "TypedEvaluableAdapter{" +
			"signature=" + signature +
			", delegate=" + delegate +
			'}';
	}
}
