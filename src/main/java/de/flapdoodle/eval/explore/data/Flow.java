package de.flapdoodle.eval.explore.data;

import de.flapdoodle.eval.explore.types.FlowId;

import java.util.List;

public interface Flow<T> {
	FlowId<T> id();
	List<ChangeList<T>> transactions();
}
