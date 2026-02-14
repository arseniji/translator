package dot.trans.lex_analyser;

import java.util.HashMap;
import java.util.Map;

public class Lexems {
    public static final HashMap<String,String> OPERATORS = new HashMap<>() {{
        put("+", "O1");
        put("-", "O2");
        put("*", "O3");
        put("/", "O4");
        put("%", "O5");
        put("=", "O6");
        put("==", "O7");
        put("!=", "O8");
        put("<", "O9");
        put(">", "O10");
        put("<=", "O11");
        put(">=", "O12");
        put("&&", "O13");
        put("||", "O14");
        put("!", "O15");
        put("++", "O16");
        put("--", "O17");
        put("+=", "O18");
        put("-=", "O19");
        put("*=", "O20");
        put("/=", "O21");
        put("%=", "O22");
        put("<<", "O23");
        put(">>", "O24");
        put(".", "O25");

    }};
    public static final HashMap<String,String> KEYWORDS = new HashMap<>(){{
        put("int", "W1");
        put("float", "W2");
        put("double", "W3");
        put("char", "W4");
        put("bool", "W5");
        put("void", "W6");
        put("if", "W7");
        put("else", "W8");
        put("for", "W9");
        put("while", "W10");
        put("do", "W11");
        put("return", "W12");
        put("break", "W13");
        put("continue", "W14");
        put("switch", "W15");
        put("case", "W16");
        put("default", "W17");
        put("const", "W18");
        put("true","W19");
        put("false","W20");
        put("long", "W21");
        put("short", "W22");
        put("signed", "W23");
        put("unsigned", "W24");
        put("nullptr", "W25");
        put("cin", "W26");
        put("cout", "W27");
        put("endl", "W28");
        put("main", "W29");
        put("sqrt", "W30");
    }};
    public static final HashMap<String,String> SEPARATORS = new HashMap<>(){{
        put(";", "R1");
        put(",", "R2");
        put(":", "R3");
        put("(", "R4");
        put(")", "R5");
        put("{", "R6");
        put("}", "R7");
        put("[", "R8");
        put("]", "R9");
        put("\"", "R10");
        put("'", "R11");
    }};

    public static final HashMap<String,String> COMMENTS = new HashMap<>();
    public static final HashMap<String,String> MULTICOMMENTS = new HashMap<>();
    public static final HashMap<String,String> NUMBERS = new HashMap<>();
    public static final HashMap<String,String> STRINGS = new HashMap<>();
    public static final HashMap<String,String> VARIABLES = new HashMap<>();
    public static final HashMap<String,String> CHARS = new HashMap<>();

}
