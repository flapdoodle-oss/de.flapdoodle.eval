package de.flapdoodle.eval.core.tree;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Variable {
	@Value.Parameter
	public abstract int hash();
	@Value.Parameter
	public abstract String name();
	@Value.Parameter
	public abstract int position();


	public static Variable of(String name, int hash, int position) {
		return ImmutableVariable.of(hash, name, position);
	}
}
