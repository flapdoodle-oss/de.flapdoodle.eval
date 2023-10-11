package de.flapdoodle.eval.evaluables;

import de.flapdoodle.eval.exceptions.EvaluableException;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.exceptions.EvaluationException;
import de.flapdoodle.eval.parser.Token;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;

import java.util.List;
import java.util.Optional;

class TypedEvaluableAdapter<T extends Value<?>> implements TypedEvaluable<T> {
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
	public T evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, List<? extends Value<?>> arguments)
		throws EvaluationException {
		checkArguments(token, arguments);
		return delegate.evaluate(valueResolver, evaluationContext, token, arguments);
	}

	protected void checkArguments(Token token, List<? extends Value<?>> arguments) throws EvaluationException {
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
