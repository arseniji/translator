package dot.trans.token_util;

import dot.trans.lexem.TokenType;

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

    public String getGroup(){
        return type.getGroup();
    }

    public boolean isType() {
        return Set.of(TokenType.INT, TokenType.FLOAT, TokenType.DOUBLE,
                        TokenType.CHAR, TokenType.BOOL,  TokenType.VOID,
                        TokenType.LONG, TokenType.SHORT)
                .contains(type);
    }
    public boolean isBool() {
        return type == TokenType.TRUE || type == TokenType.FALSE;
    }

    public boolean isRightAssociative() {
        return switch (type) {
            case ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN,
                 MUL_ASSIGN, DIV_ASSIGN, MOD_ASSIGN,
                 UNARY_MINUS, NOT -> true;
            default -> false;
        };
    }

    public TokenType getType() {
        return type;
    }
    public int getIndex() { return index; }
    public String getValue() { return value; }

    public boolean isOperator() {
        return type.getCode().startsWith("O") || type == TokenType.UNARY_MINUS;
    }

    @Override
    public String toString() {
        String code = index >= 0 ? type.getCode() + "[" + index + "]" : type.getCode();
        return String.format("Token(%s, '%s')", code, value);
    }
}