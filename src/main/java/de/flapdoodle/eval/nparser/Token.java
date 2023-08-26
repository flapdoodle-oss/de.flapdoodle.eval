package de.flapdoodle.eval.nparser;

import de.flapdoodle.eval.parser.TokenType;
import org.immutables.value.Value;

@Value.Immutable
public interface Token {

    @Value.Parameter
    int start();

    @Value.Parameter
    String value();

    @Value.Parameter
    TokenType type();

    static Token of(int startPosition, String value, TokenType type) {
        return ImmutableToken.of(startPosition, value, type);
    }
}
