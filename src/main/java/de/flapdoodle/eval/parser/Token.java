package de.flapdoodle.eval.parser;

import de.flapdoodle.eval.CommonToken;
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
