package dot.trans.lex_analyser;

import dot.trans.lexem.TokenType;
import dot.trans.token_util.Token;
import dot.trans.token_util.TokenList;

public class UnaryResolver {

    public static TokenList resolve(TokenList input) {
        TokenList result = new TokenList();

        for (int i = 0; i < input.size(); i++) {
            Token current = input.getToken(i);

            if (current.getType() == TokenType.MINUS && isUnaryPosition(result)) {
                result.addToken(new Token(TokenType.UNARY_MINUS, "-"));
            } else {
                result.addToken(current);
            }
        }
        return result;
    }

    private static boolean isUnaryPosition(TokenList result) {
        if (result.isEmpty()) return true;

        Token prev = result.getToken(result.size() - 1);
        String group = prev.getGroup();

        return switch (group) {
            case "O", "U" -> true;
            case "R" -> prev.getType() == TokenType.LPAREN;
            default -> false;
        };
    }
}