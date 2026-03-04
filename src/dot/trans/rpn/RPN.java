package dot.trans.rpn;

import dot.trans.lexem.TokenType;
import dot.trans.token_util.Token;
import dot.trans.token_util.TokenList;

import java.util.ArrayDeque;

import static dot.trans.rpn.PriorityTable.priorityTable;

public class RPN {
    private ArrayDeque<Token> stack = new ArrayDeque<>();
    private TokenList exitString = new TokenList();

    public TokenList processArray(TokenList tokenList) {
        for (int i = 0; i < tokenList.size(); i++) {
            add(tokenList.getToken(i));
        }
        while (!stack.isEmpty()) {
            exitString.addToken(stack.pop());
        }
        return exitString;
    }

    private void add(Token token) {
        switch (token.getGroup()) {
            case "N", "V", "S", "H" -> exitString.addToken(token);
            case "W" -> { if (token.isBool()) exitString.addToken(token); }
            case "U", "O" -> processOperator(token);
            case "E" -> { /* пропуск */ }
            case "R" -> {
                if (token.getType() == TokenType.LPAREN)       handleOpenBracket(token);
                else if (token.getType() == TokenType.RPAREN)  handleClosedBracket();
            }
        }
    }

    private void processOperator(Token token) {
        // убрано лишнее token.isOperator() — мы уже в ветке O/U
        while (!stack.isEmpty() && hasHigherPrecedence(stack.peek(), token)) {
            exitString.addToken(stack.pop());
        }
        stack.push(token);
    }

    private void handleOpenBracket(Token token) {
        stack.push(token);
    }

    private void handleClosedBracket() {
        while (!stack.isEmpty()
                && stack.peek().getType() != TokenType.LPAREN) {
            exitString.addToken(stack.pop());
        }
        if (!stack.isEmpty()) stack.pop(); // убираем '('
    }

    private boolean hasHigherPrecedence(Token top, Token current) {
        Integer pTop     = priorityTable.get(top.getType());
        Integer pCurrent = priorityTable.get(current.getType());
        if (pTop == null || pCurrent == null) return false;
        if (top.getType() == TokenType.LPAREN) return false;

        return current.isRightAssociative()
                ? pTop > pCurrent
                : pTop >= pCurrent;
    }

    public void clear() {
        stack.clear();
        exitString.clear();
    }
}