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

    public String display() {
        return switch (type) {
            case JMP  -> "JMP";
            case JZ   -> "JZ";
            case JNZ  -> "JNZ";

            case LABEL -> value.equals("LABEL") ? "L" + index + ":" : "L" + index;

            case NUMBER   -> "N" + index;
            case STRING   -> "S" + index;
            case VARIABLE -> "V" + index;
            case CHAR_LIT -> "H" + index;

            case FUNC_BEGIN -> (index > 0 ? index + " " : "") + value;
            case CALL       -> index + " " + value;
            case NOP        -> index >= 0 ? index + " " + value : value;

            case DECL, ALLOC, RETURN_VAL,
                 INDEX, PARAM, ADDR, STOP -> value;

            default -> {
                if (index >= 0) yield type.getCode() + index;
                else yield type.getCode();
            }
        };
    }

    public void sh() {
        System.out.print(display() + " ");
    }

    @Override
    public String toString() {
        String code = index >= 0 ? type.getCode() + "[" + index + "]" : type.getCode();
        return String.format("Token(%s, '%s')", code, value);
    }
}