package de.flapdoodle.eval.operators;

import de.flapdoodle.eval.CommonToken;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

import java.util.Optional;

public class Evaluator {
	private final CommonToken operatorToken;
	private final Value<?> leftOperand;
	private final Value<?> rightOperand;
	private Optional<Value<?>> result = Optional.empty();

	public Evaluator(CommonToken operatorToken, Value<?> leftOperand, Value<?> rightOperand) {
		this.operatorToken = operatorToken;
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public interface Evaluation<L, R> {
		Value<?> evaluate(L left, R right) throws EvaluationException;
	}

	public <L extends Value<?>, R extends Value<?>> Evaluator using(Class<L> leftType, Class<R> rightType, Evaluation<L, R> function)
		throws EvaluationException {
		if (!result.isPresent() && leftType.isInstance(leftOperand) && rightType.isInstance(rightOperand)) {
			result = Optional.of(function.evaluate(leftType.cast(leftOperand), rightType.cast(rightOperand)));
		}
		return this;
	}

	public Value<?> get() throws EvaluationException {
		if (!result.isPresent()) {
			throw new EvaluationException(operatorToken, "could not evaluate " + leftOperand + ", " + rightOperand);
		}
		return result.get();
	}
}
