package de.flapdoodle.eval.nparser;

import de.flapdoodle.eval.CommonToken;
import de.flapdoodle.eval.parser.TokenType;
import org.immutables.value.Value;

@Value.Immutable
public interface Token extends CommonToken {

    @Value.Parameter
    @Override
    int start();

    @Value.Parameter
    @Override
    String value();

    @Value.Parameter
    @Override
    TokenType type();

    static Token of(int startPosition, String value, TokenType type) {
        return ImmutableToken.of(startPosition, value, type);
    }
}
