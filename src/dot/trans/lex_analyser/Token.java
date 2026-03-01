package dot.trans.lex_analyser;

import dot.trans.lex_analyser.lexem.TokenType;

import java.util.Set;

public class Token {
    private final TokenType type;
    private final int index;
    private final String value;

    public Token(TokenType type, String value) {
        this.type  = type;
        this.value = value;
        this.index = -1;
    }
    public Token(TokenType type, int index, String value) {
        this.type  = type;
        this.index = index;
        this.value = value;
    }
    public String getCode() {
        if (index < 0) return type.getCode();
        return type.getCode() + index;
    }
    public boolean isType() {
        return Set.of(TokenType.INT, TokenType.FLOAT, TokenType.DOUBLE,
                        TokenType.CHAR, TokenType.BOOL,  TokenType.VOID,
                        TokenType.LONG, TokenType.SHORT)
                .contains(type);
    }

    public boolean isOperator() {
        return type.getCode().startsWith("O") || type == TokenType.UNARY_MINUS;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, '%s')", getCode(), value);
    }
}