package de.flapdoodle.eval.operators.booleans;

import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.AbstractInfixOperator;
import de.flapdoodle.eval.operators.Precedence;

public abstract class AbstractBooleanInfixOperator extends AbstractInfixOperator.Typed<Value.BooleanValue, Value.BooleanValue> {
	protected AbstractBooleanInfixOperator(Precedence precedence, boolean leftAssociative) {
		super(precedence, leftAssociative, Value.BooleanValue.class, Value.BooleanValue.class);
	}
	
	protected AbstractBooleanInfixOperator(Precedence precedence) {
		super(precedence, Value.BooleanValue.class, Value.BooleanValue.class);
	}
}
