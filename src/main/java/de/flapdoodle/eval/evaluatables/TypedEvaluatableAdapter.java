package de.flapdoodle.eval.evaluatables;

import de.flapdoodle.eval.EvaluatableException;
import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.parser.Token;

import java.util.List;
import java.util.Optional;

class TypedEvaluatableAdapter<T extends Value<?>> implements TypedEvaluatable<T> {
	private final Signature<T> signature;
	private final Evaluatable<T> delegate;

	public TypedEvaluatableAdapter(Signature<T> signature, Evaluatable<T> delegate) {
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
		Optional<EvaluatableException> error = signature().validateArguments(arguments);
		if (error.isPresent()) {
			throw new EvaluationException(token, error.get());
		}
	}

	@Override
	public String toString() {
		return "TypedEvaluatableAdapter{" +
			"signature=" + signature +
			", delegate=" + delegate +
			'}';
	}
}
