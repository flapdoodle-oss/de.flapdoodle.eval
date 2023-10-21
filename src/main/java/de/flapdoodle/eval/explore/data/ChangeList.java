package de.flapdoodle.eval.explore.data;

import java.util.List;

public interface ChangeList<T> {
	List<Change<T>> changes();
}
