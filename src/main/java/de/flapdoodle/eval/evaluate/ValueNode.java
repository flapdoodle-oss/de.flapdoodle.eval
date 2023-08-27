package de.flapdoodle.eval.evaluate;

import de.flapdoodle.eval.data.Value;

public abstract class ValueNode<T> extends Node {

	public static <T> ValueNode<T> of(Value<T> value) {
		return null;
	}

}
