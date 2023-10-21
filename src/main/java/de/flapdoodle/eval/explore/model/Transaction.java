package de.flapdoodle.eval.explore.model;

import de.flapdoodle.eval.explore.data.Change;
import org.immutables.value.Value;

@Value.Immutable
public interface Transaction<S, D> {
	@Value.Parameter
	Change<S> source();
	@Value.Parameter
	Change<D> destination();

	static <S, D> Transaction<S, D> of(Change<S> source, Change<D> dest) {
		return ImmutableTransaction.of(source,dest);
	}
}
