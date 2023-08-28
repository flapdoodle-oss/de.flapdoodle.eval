package de.flapdoodle.eval.config;

import de.flapdoodle.eval.operators.Operator;

public interface HasOperator {
	boolean hasStartingWith(Class<? extends Operator> type, String value);
	boolean matching(Class<? extends Operator> type, String value);
}
