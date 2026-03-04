package dot.trans.rpn;

import dot.trans.lexem.TokenType;

import java.util.HashMap;

public class PriorityTable {
    public static HashMap<TokenType,Integer> priorityTable = new HashMap<>(){{
        put(TokenType.LPAREN, 0);  // (
        put(TokenType.RPAREN, 1);  // )
        put(TokenType.LBRACKET, 0);  // [
        put(TokenType.RBRACKET, 1);  // ]
        put(TokenType.COMMA, 1);  // ,

        put(TokenType.ASSIGN, 2);   // =
        put(TokenType.PLUS_ASSIGN, 2);  // +=
        put(TokenType.MINUS_ASSIGN, 2);  // -=
        put(TokenType.MUL_ASSIGN, 2);  // *=
        put(TokenType.DIV_ASSIGN, 2);  // /=
        put(TokenType.MOD_ASSIGN, 2);  // %=

        put(TokenType.OR, 3);  // ||

        put(TokenType.AND, 4);  // &&

        put(TokenType.EQ, 5);   // ==
        put(TokenType.NEQ, 5);   // !=

        put(TokenType.LT, 6);   // <
        put(TokenType.GT, 6);  // >
        put(TokenType.LEQ, 6);  // <=
        put(TokenType.GEQ, 6);  // >=


        put(TokenType.LSHIFT, 7);  // <<
        put(TokenType.RSHIFT, 7);  // >>

        put(TokenType.PLUS, 8);   // +
        put(TokenType.MINUS, 8);   // - (бинарный)

        put(TokenType.MUL, 9);   // *
        put(TokenType.DIV, 9);   // /
        put(TokenType.MOD, 9);   // %

        put(TokenType.UNARY_MINUS, 10);  // - (унарный)
        put(TokenType.NOT, 10); // ! (логическое НЕ)
        put(TokenType.INC, 10); // ++ (префикс/постфикс)
        put(TokenType.DEC, 10); // -- (префикс/постфикс)

        put(TokenType.DOT, 11); // . (точка)
    }};
}