package dot.trans.lex_analyser;

import dot.trans.lexem.Lexems;
import dot.trans.lexem.TokenType;
import dot.trans.token_util.Token;

public class TokenMapper {
    public static Token add(TokenType type, String value) {
        Token token = switch (type) {
            case VARIABLE -> {
                int idx = Lexems.VARIABLES.computeIfAbsent(value, v -> Lexems.VARIABLES.size() + 1);
                yield new Token(type, idx, value);
            }
            case NUMBER -> {
                int idx = Lexems.NUMBERS.computeIfAbsent(value, v -> Lexems.NUMBERS.size() + 1);
                yield new Token(type, idx, value);
            }
            case STRING -> {
                int idx = Lexems.STRINGS.computeIfAbsent(value, v -> Lexems.STRINGS.size() + 1);
                yield new Token(type, idx, value);
            }
            case CHAR_LIT -> {
                int idx = Lexems.CHARS.computeIfAbsent(value, v -> Lexems.CHARS.size() + 1);
                yield new Token(type, idx, value);
            }
            case COMMENT -> {
                int idx = Lexems.COMMENTS.computeIfAbsent(value, v -> Lexems.COMMENTS.size() + 1);
                yield new Token(type, idx, value);
            }
            case MULTICOMMENT -> {
                int idx = Lexems.MULTICOMMENTS.computeIfAbsent(value, v -> Lexems.MULTICOMMENTS.size() + 1);
                yield new Token(type, idx, value);
            }

            default -> new Token(type, value);
        };
        return token;
    }
}
