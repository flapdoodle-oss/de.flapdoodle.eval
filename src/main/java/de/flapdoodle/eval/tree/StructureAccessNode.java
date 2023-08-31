package de.flapdoodle.eval.tree;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.config.ValueResolver;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.parser.Token;

@org.immutables.value.Value.Immutable
public abstract class StructureAccessNode extends Node {
	@org.immutables.value.Value.Parameter
	protected abstract Node structure();
	@org.immutables.value.Value.Parameter
	protected abstract Token nameToken();

	@Override
	public Value<?> evaluate(ValueResolver variableResolver, EvaluationContext context) throws EvaluationException {
		Value<?> structure = structure().evaluate(variableResolver, context);
		String name=nameToken().value();

		if (structure instanceof Value.MapValue) {
			Value.MapValue map = (Value.MapValue) structure;
			if (!map.wrapped().containsKey(name)) {
				throw new EvaluationException(
					nameToken(), String.format("Field '%s' not found in structure", name));
			}
			return map.wrapped().get(name);
		} else {
			throw EvaluationException.ofUnsupportedDataTypeInOperation(token());
		}
	}

	public static StructureAccessNode of(Token token, Node structure, Token nameToken) {
		return ImmutableStructureAccessNode.builder()
			.token(token)
			.structure(structure)
			.nameToken(nameToken)
			.build();
	}
}
