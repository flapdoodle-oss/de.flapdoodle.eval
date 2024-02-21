package de.flapdoodle.eval.core.tree;

import de.flapdoodle.checks.Preconditions;
import de.flapdoodle.types.Pair;
import org.immutables.value.Value;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Value.Immutable
public abstract class VariableNames {
	// multiple entries with different hashes for same name
	protected abstract List<Pair<Integer, String>> map();

	@Value.Derived
	public Map<String, Integer> nameHashMap() {
		Map<String, Integer> map=new LinkedHashMap<>();
		map().forEach(pair -> map.putIfAbsent(pair.second(), pair.first()));
		return map;
	}

	@Value.Derived
	public Set<String> names() {
		return nameHashMap().keySet();
	}

	@Value.Auxiliary
	public int hashOf(String name) {
		return Preconditions.checkNotNull(nameHashMap().get(name),"could not find hash of %s", name);
	}

	public static ImmutableVariableNames.Builder builder() {
		return ImmutableVariableNames.builder();
	}

}
