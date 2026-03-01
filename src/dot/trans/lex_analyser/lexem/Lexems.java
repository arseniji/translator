package dot.trans.lex_analyser.lexem;

import java.util.LinkedHashMap;
import java.util.Map;

public class Lexems {

    // keyword string → TokenType
    public static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
            Map.entry("int",      TokenType.INT),
            Map.entry("float",    TokenType.FLOAT),
            Map.entry("double",   TokenType.DOUBLE),
            Map.entry("char",     TokenType.CHAR),
            Map.entry("bool",     TokenType.BOOL),
            Map.entry("void",     TokenType.VOID),
            Map.entry("if",       TokenType.IF),
            Map.entry("else",     TokenType.ELSE),
            Map.entry("for",      TokenType.FOR),
            Map.entry("while",    TokenType.WHILE),
            Map.entry("do",       TokenType.DO),
            Map.entry("return",   TokenType.RETURN),
            Map.entry("break",    TokenType.BREAK),
            Map.entry("continue", TokenType.CONTINUE),
            Map.entry("const",    TokenType.CONST),
            Map.entry("true",     TokenType.TRUE),
            Map.entry("false",    TokenType.FALSE),
            Map.entry("long",     TokenType.LONG),
            Map.entry("short",    TokenType.SHORT),
            Map.entry("sqrt",     TokenType.SQRT),
            Map.entry("cin",      TokenType.CIN),
            Map.entry("cout",     TokenType.COUT),
            Map.entry("endl",     TokenType.ENDL),
            Map.entry("nullptr",  TokenType.NULLPTR)
    );

    public static final Map<String, TokenType> OPERATORS = Map.ofEntries(
            Map.entry("+",  TokenType.PLUS),
            Map.entry("-",  TokenType.MINUS),
            Map.entry("*",  TokenType.MUL),
            Map.entry("/",  TokenType.DIV),
            Map.entry("%",  TokenType.MOD),
            Map.entry("=",  TokenType.ASSIGN),
            Map.entry("==", TokenType.EQ),
            Map.entry("!=", TokenType.NEQ),
            Map.entry("<",  TokenType.LT),
            Map.entry(">",  TokenType.GT),
            Map.entry("<=", TokenType.LEQ),
            Map.entry(">=", TokenType.GEQ),
            Map.entry("&&", TokenType.AND),
            Map.entry("||", TokenType.OR),
            Map.entry("!",  TokenType.NOT),
            Map.entry("++", TokenType.INC),
            Map.entry("--", TokenType.DEC),
            Map.entry("+=", TokenType.PLUS_ASSIGN),
            Map.entry("-=", TokenType.MINUS_ASSIGN),
            Map.entry("*=", TokenType.MUL_ASSIGN),
            Map.entry("/=", TokenType.DIV_ASSIGN),
            Map.entry("%=", TokenType.MOD_ASSIGN),
            Map.entry("<<", TokenType.LSHIFT),
            Map.entry(">>", TokenType.RSHIFT),
            Map.entry(".",  TokenType.DOT)
    );

    public static final Map<String, TokenType> SEPARATORS = Map.ofEntries(
            Map.entry(";",  TokenType.SEMICOLON),
            Map.entry(",",  TokenType.COMMA),
            Map.entry(":",  TokenType.COLON),
            Map.entry("(",  TokenType.LPAREN),
            Map.entry(")",  TokenType.RPAREN),
            Map.entry("{",  TokenType.LBRACE),
            Map.entry("}",  TokenType.RBRACE),
            Map.entry("[",  TokenType.LBRACKET),
            Map.entry("]",  TokenType.RBRACKET)
    );

    // динамические таблицы — значение → присвоенный номер
    public static final Map<String, Integer> NUMBERS    = new LinkedHashMap<>();
    public static final Map<String, Integer> STRINGS    = new LinkedHashMap<>();
    public static final Map<String, Integer> VARIABLES  = new LinkedHashMap<>();
    public static final Map<String, Integer> CHARS      = new LinkedHashMap<>();
    public static final Map<String, Integer> COMMENTS   = new LinkedHashMap<>();
    public static final Map<String, Integer> MULTICOMMENTS = new LinkedHashMap<>();
}