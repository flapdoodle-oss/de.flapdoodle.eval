package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.parser.Token;

@org.immutables.value.Value.Immutable
public abstract class ArrayAccessNode extends Node {
	@org.immutables.value.Value.Parameter
	protected abstract Node array();
	@org.immutables.value.Value.Parameter
	protected abstract Node index();

	@Override
	public Value<?> evaluate(ValueResolver variableResolver, EvaluationContext context) throws EvaluationException {
		Value<?> array = array().evaluate(variableResolver, context);
		Value<?> index = index().evaluate(variableResolver, context);

		if (array instanceof Value.ArrayValue && index instanceof Value.NumberValue) {
			return ((Value.ArrayValue) array).wrapped().get(((Value.NumberValue) index).wrapped().intValue());
		} else {
			throw EvaluationException.ofUnsupportedDataTypeInOperation(token());
		}
	}

	public static ArrayAccessNode of(Token token, Node array, Node index) {
		return ImmutableArrayAccessNode.builder()
			.token(token)
			.array(array)
			.index(index)
			.build();
	}
}
