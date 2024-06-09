package de.flapdoodle.eval.core.tree;

import de.flapdoodle.checks.Preconditions;
import org.immutables.value.Value;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Value.Immutable
public abstract class Variables {
	// multiple entries with different hashes for same name
	public abstract List<Variable> list();

	@Value.Derived
	public Map<String, Integer> nameHashMap() {
		Map<String, Integer> map=new LinkedHashMap<>();
		list().forEach(pair -> map.putIfAbsent(pair.name(), pair.hash()));
		return map;
	}

	@Value.Derived
	public Set<String> names() {
		return nameHashMap().keySet();
	}

	@Value.Auxiliary
	public int firstHashOf(String name) {
		return Preconditions.checkNotNull(nameHashMap().get(name),"could not find hash of %s", name);
	}

	@Value.Auxiliary
	@Deprecated
	public int hashOf(String name) {
		return firstHashOf(name);
	}

	public static ImmutableVariables.Builder builder() {
		return ImmutableVariables.builder();
	}
}
