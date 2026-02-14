package dot.trans.lex_analyser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private String input;
    private Integer position;
    private Integer line;
    private Integer lineStart;
    private List<TokenMatcher> TOKEN_PATTERNS = new ArrayList<>();
    private TokenList tokenString;

    public Tokenizer(String input){
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.lineStart = 0;
        this.tokenString = new TokenList();

        initTokenPatterns();
        for (TokenMatcher tm: TOKEN_PATTERNS){
            tm.initMatcher(input);
        }
    }
    private void initTokenPatterns(){
        // многострочные ком
        TOKEN_PATTERNS.add(new TokenMatcher("M", "/\\*.*?\\*/", true, Pattern.DOTALL));

        // комментарии
        TOKEN_PATTERNS.add(new TokenMatcher("C", "//.*", true));

        // пробелы (игнорируем)
        TOKEN_PATTERNS.add(new TokenMatcher("WHITESPACE", "\\s+", false));

        // #include
        TOKEN_PATTERNS.add(new TokenMatcher("PREPROCESSOR",
                "^\\s*#include\\s*(<[^>]+>|\"[^\"]+\")\\s*$",
                false, Pattern.MULTILINE));

        // #define
        TOKEN_PATTERNS.add(new TokenMatcher("PREPROCESSOR",
                "^\\s*#define\\s+[a-zA-Z_][a-zA-Z0-9_]*.*$",
                false, Pattern.MULTILINE));

        // #ifdef, #ifndef, #endif, #pragma ...
        TOKEN_PATTERNS.add(new TokenMatcher("PREPROCESSOR",
                "^\\s*#(if|ifdef|ifndef|else|elif|endif|pragma|undef)\\b.*$",
                false, Pattern.MULTILINE));

        // no namespace
        TOKEN_PATTERNS.add(new TokenMatcher("USING_NAMESPACE",
                "^\\s*using\\s+namespace\\s+[a-zA-Z_][a-zA-Z0-9_]*(::[a-zA-Z_][a-zA-Z0-9_]*)*\\s*;\\s*$",
                false, Pattern.MULTILINE));

        // строковые и символьные литералы
        TOKEN_PATTERNS.add(new TokenMatcher("S", "\"(\\\\\"|[^\"\n])*\"", true));
        TOKEN_PATTERNS.add(new TokenMatcher("H", "'(\\\\'|[^'\n])*'", true));

        // числовые литералы
        TOKEN_PATTERNS.add(new TokenMatcher("N", "0[xX][0-9a-fA-F]+[uUlL]?", true));
        TOKEN_PATTERNS.add(new TokenMatcher("N", "0[0-7]+[uUlL]?", true));
        TOKEN_PATTERNS.add(new TokenMatcher("N", "(?:\\d+\\.\\d*|\\.\\d+)(?:[eE][+-]?\\d+)?[fFlL]?|\\d+[eE][+-]?\\d+[fFlL]?", true));
        TOKEN_PATTERNS.add(new TokenMatcher("N", "\\d+[uUlL]{0,3}", true));

        // операторы
        TOKEN_PATTERNS.add(new TokenMatcher("O",
                "->\\*|\\.\\*|<<=|>>=|\\+\\+|--|->|<<|>>|<=|>=|==|!=|&&|\\|\\||" +
                        "\\+=|-=|\\*=|/=|%=|&=|\\|=|^=|::|" +
                        "[+\\-*/%&|^~!<>=]", true));

        // Разделители (отдельные символы)
        TOKEN_PATTERNS.add(new TokenMatcher("R", "[;,.:(){}\\[\\]]", true));

        // Ключевые слова и идентификаторы
        TOKEN_PATTERNS.add(new TokenMatcher("W",
                "\\b(sqrt|if|main|int|float|double|char|bool|void|long|short|signed|unsigned|main" +
                        "if|else|for|while|do|switch|case|default|break|continue|return|" +
                        "const|true|false|nullptr|cin|cout|endl|class|struct|template|" +
                        "public|private|protected|this|namespace|using|auto|static|extern)\\b",
                true));

        // идентифекаторы
        TOKEN_PATTERNS.add(new TokenMatcher("V", "[a-zA-Z_][a-zA-Z0-9_]*", true));
    }

    public TokenList varTokenize() {
        int iteration = 0;
        while (position != input.length()){
            iteration ++;
            boolean matched = false;
            for (TokenMatcher tm : TOKEN_PATTERNS){
                if (tm.lookingAt(position)){
                    String value = tm.getMatchedText();
                    int endPosition = tm.getMatchedEnd();
                    matched = true;
                    int col = position - lineStart + 1;

                    if (tm.keep) {tokenString.add(tm.type,value,line,col);}

                    int newLines = 0;
                    for (int i = position; i < endPosition; i++){
                        if (input.charAt(i) == '\n'){
                            newLines ++;
                            lineStart = i + 1;
                        }
                    }
                    line += newLines;
                    position = endPosition;
                    break;
                }
            }
            if (!matched){
                int col = position - lineStart + 1;
                tokenString.add("ERROR", String.valueOf(input.charAt(position)),line,col);
                position++;
            }
        }
        tokenString.add("EOF", "", line, position - lineStart + 1);
        return tokenString;
    }

}
