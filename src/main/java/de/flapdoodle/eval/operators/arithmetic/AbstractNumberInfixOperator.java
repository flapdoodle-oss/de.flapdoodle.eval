package de.flapdoodle.eval.operators.arithmetic;

import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.AbstractInfixOperator;
import de.flapdoodle.eval.operators.Precedence;

public abstract class AbstractNumberInfixOperator extends AbstractInfixOperator.Typed<Value.NumberValue, Value.NumberValue> {
	protected AbstractNumberInfixOperator(Precedence precedence, boolean leftAssociative) {
		super(precedence, leftAssociative, Value.NumberValue.class, Value.NumberValue.class);
	}
	protected AbstractNumberInfixOperator(Precedence precedence) {
		super(precedence, Value.NumberValue.class, Value.NumberValue.class);
	}
}
