package de.flapdoodle.eval.explore.calculation;

import de.flapdoodle.eval.explore.types.FlowId;
import de.flapdoodle.eval.explore.types.FlowState;

public interface FlowStateLookup {
	<T> FlowState<T> stateOf(FlowId<T> id);
}
