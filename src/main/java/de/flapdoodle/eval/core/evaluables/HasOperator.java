package de.flapdoodle.eval.core.evaluables;

public interface HasOperator {
	boolean hasStartingWith(OperatorType type, String value);
	boolean matching(OperatorType type, String value);
}
