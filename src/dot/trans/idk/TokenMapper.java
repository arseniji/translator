package dot.trans.idk;

import dot.trans.lex_analyser.Lexems;
import dot.trans.lex_analyser.Token;

import java.util.ArrayList;
import java.util.List;

public class TokenMapper {
    private int numberCounter = 1;
    private int stringCounter = 1;
    private int varCounter = 1;
    private int charCounter = 1;
    private int commentCounter = 1;
    private int multicommentCounter = 1;

    public String tokenMap(Token token) {
        switch (token.type) {
            case "OPERATOR":
                return Lexems.OPERATORS.getOrDefault(token.value, "O?");
            case "SEPARATOR":
                return Lexems.SEPARATORS.getOrDefault(token.value, "R?");
            case "KEYWORD":
                return Lexems.KEYWORDS.getOrDefault(token.value, "W?");
            case "COMMENT":
                String commentKey = token.value;
                if (!Lexems.COMMENTS.containsKey(commentKey)) {
                    Lexems.COMMENTS.put(commentKey, "C" + commentCounter++);
                }
                return Lexems.COMMENTS.get(commentKey);
            case "MULTILINE_COMMENT":
                String multicommentKey = token.value;
                if (!Lexems.MULTICOMMENTS.containsKey(multicommentKey)) {
                    Lexems.MULTICOMMENTS.put(multicommentKey, "M" + multicommentCounter++);
                }
                return Lexems.MULTICOMMENTS.get(multicommentKey);
            case "INTEGER_LITERAL":
            case "FLOAT_LITERAL":
            case "HEX_LITERAL":
            case "OCTAL_LITERAL":
                String numKey = token.value;
                if (!Lexems.NUMBERS.containsKey(numKey)) {
                    Lexems.NUMBERS.put(numKey, "N" + numberCounter++);
                }
                return Lexems.NUMBERS.get(numKey);
            case "STRING_LITERAL":
                String strKey = token.value;
                if (!Lexems.STRINGS.containsKey(strKey)) {
                    Lexems.STRINGS.put(strKey, "S" + stringCounter++);
                }
                return Lexems.STRINGS.get(strKey);
            case "CHAR_LITERAL":
                String charKey = token.value;
                if (!Lexems.CHARS.containsKey(charKey)) {
                    Lexems.CHARS.put(charKey, "H" + charCounter++);
                }
                return Lexems.CHARS.get(charKey);
            case "IDENTIFIER":
                String varKey = token.value;
                if (!Lexems.VARIABLES.containsKey(varKey)) {
                    Lexems.VARIABLES.put(varKey, "V" + varCounter++);
                }
                return Lexems.VARIABLES.get(varKey);
            case "WHITESPACE":
                return null;
            case "ERROR":
                return "ERR";
            case "EOF":
                return "EOF";
            default:
                return "?";
        }
    }

    public List<String> buildLexifiedArray(List<Token> tokens){
        List<String> result = new ArrayList<>();
        for(Token token: tokens){
            result.add(tokenMap(token));
        }
        return result;
    }
}