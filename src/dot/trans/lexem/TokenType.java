package dot.trans.lexem;

public enum TokenType {
    INT("W1"), FLOAT("W2"), DOUBLE("W3"), CHAR("W4"), BOOL("W5"),
    VOID("W6"), IF("W7"), ELSE("W8"), FOR("W9"), WHILE("W10"),
    DO("W11"), RETURN("W12"), BREAK("W13"), CONTINUE("W14"),
    CONST("W18"), TRUE("W19"), FALSE("W20"), LONG("W21"),
    SHORT("W22"), SIGNED("W23"), UNSIGNED("W24"), NULLPTR("W25"),
    CIN("W26"), COUT("W27"), ENDL("W28"), SQRT("W29"),

    PLUS("O1"), MINUS("O2"), MUL("O3"), DIV("O4"), MOD("O5"),
    ASSIGN("O6"), EQ("O7"), NEQ("O8"), LT("O9"), GT("O10"),
    LEQ("O11"), GEQ("O12"), AND("O13"), OR("O14"), NOT("O15"),
    INC("O16"), DEC("O17"), PLUS_ASSIGN("O18"), MINUS_ASSIGN("O19"),
    MUL_ASSIGN("O20"), DIV_ASSIGN("O21"), MOD_ASSIGN("O22"),
    LSHIFT("O23"), RSHIFT("O24"), DOT("O25"),

    SEMICOLON("R1"), COMMA("R2"), COLON("R3"),
    LPAREN("R4"), RPAREN("R5"), LBRACE("R6"), RBRACE("R7"),
    LBRACKET("R8"), RBRACKET("R9"), DQUOTE("R10"), SQUOTE("R11"),

    NUMBER("N"), STRING("S"), VARIABLE("V"), CHAR_LIT("H"),
    COMMENT("C"), MULTICOMMENT("M"), UNARY_MINUS("U2"),
    ERROR("E"),

    JMP("P1"), JZ("P2"), JNZ("P3"), LABEL("P4"),
    DECL("P5"), ALLOC("P6"),
    CALL("P7"), RETURN_VAL("P8"), FUNC_BEGIN("P9"),
    FUNC_END("P10"), PARAM("P11"),
    INDEX("P12"), ADDR("P13"),
    NOP("P14"), STOP("P15");

    private final String code;
    TokenType(String code) { this.code = code; }
    public String getCode() { return code; }

    public String getGroup() {return String.valueOf(code.charAt(0));}
}