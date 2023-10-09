package de.flapdoodle.eval.evaluatables;

public interface HasOperator {
	boolean hasStartingWith(OperatorType type, String value);
	boolean matching(OperatorType type, String value);
}
