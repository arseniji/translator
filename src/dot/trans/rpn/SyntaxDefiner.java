package dot.trans.rpn;

import dot.trans.lexem.TokenType;
import dot.trans.token_util.Token;
import dot.trans.token_util.TokenList;

import java.util.ArrayDeque;

public class SyntaxDefiner {
    private TokenList enterList;
    private TokenList exitList = new TokenList();
    private ArrayDeque<JumpLabel> labelStack = new ArrayDeque<>();
    private RPN rpn = new RPN();
    private int position = 0;
    private int labelCounter = 0;

    private Token newLabel() {
        return new Token(TokenType.LABEL, ++labelCounter, "L" + labelCounter);
    }

    private static class JumpLabel {
        Token token;
        String type;
        int startPosition;

        JumpLabel(Token token, String type, int startPosition) {
            this.token = token;
            this.type = type;
            this.startPosition = startPosition;
        }
    }

    public SyntaxDefiner(TokenList enterList) {
        this.enterList = enterList;
    }

    public TokenList processList() {
        while (hasMoreTokens() && current() != null) {
            processStatement();
        }
        return exitList;
    }

    private boolean is(Token t, TokenType type) {
        return t != null && t.getType() == type;
    }

    private boolean cur(TokenType type) {
        return is(current(), type);
    }

    private boolean isArrayAccess() {
        return is(peek(1), TokenType.LBRACKET);
    }

    private boolean isFuncCall() {
        Token c = current();
        Token n = peek(1);
        if (c == null || n == null) return false;
        return (c.getGroup().equals("V") || is(c, TokenType.SQRT))
                && is(n, TokenType.LPAREN);
    }

    private boolean isVarDec() {
        Token c = current();
        if (c == null || !c.isType()) return false;
        return peek(1) != null && peek(1).getGroup().equals("V");
    }

    private boolean isFuncDec() {
        Token c = current();
        if (c == null || !c.isType()) return false;
        if (peek(1) == null || !peek(1).getGroup().equals("V")) return false;
        return is(peek(2), TokenType.LPAREN);
    }

    private void processStatement() {
        Token token = current();
        if (token == null) return;

        switch (token.getGroup()) {
            case "W":
                if (token.isBool())   handleExpression();
                else if (isFuncCall()) handleFuncCall();
                else                   handleKeyword();
                break;
            case "V":
                if (isArrayAccess())  handleArrayAccess();
                else if (isFuncCall()) handleFuncCall();
                else                   handleExpression();
                break;
            case "R":
                if (is(token, TokenType.RBRACE) || is(token, TokenType.SEMICOLON)) {
                    advance();
                    return;
                }
                handleExpression();
                break;
            case "C":
            case "M":
                advance();
                break;
            default:
                handleExpression();
                break;
        }
    }

    private void handleKeyword() {
        Token keyword = current();

        if (keyword.isType()) {
            if (isFuncDec())       handleFuncDec();
            else if (isVarDec())   handleVarDec();
            else                   advance();
            return;
        }

        switch (keyword.getType()) {
            case IF       -> handleIf();
            case FOR      -> handleFor();
            case WHILE    -> handleWhile();
            case DO       -> handleDoWhile();
            case RETURN   -> handleReturn();
            case BREAK    -> handleBreak();
            case CONTINUE -> handleContinue();
            case CIN      -> handleCin();
            case COUT     -> handleCout();
            default       -> advance();
        }
    }

    private void handleVarDec() {
        Token type = advance();
        exitList.addToken(type);

        Token varName = advance();
        exitList.addToken(varName);

        if (cur(TokenType.LBRACKET)) {
            advance();
            Token size = advance();
            advance();
            exitList.addToken(size);
            exitList.addToken(new Token(TokenType.ALLOC, "ARRAY_DECL"));
            if (cur(TokenType.SEMICOLON)) advance();
            return;
        }

        if (cur(TokenType.ASSIGN)) {
            advance();

            if (isFuncCall()) {
                handleFuncCall();
                exitList.addToken(new Token(TokenType.DECL, "DECL_INIT"));
            } else {
                TokenList value = extractUntil(TokenType.SEMICOLON);
                rpn.clear();
                exitList.addList(rpn.processArray(value));
                exitList.addToken(new Token(TokenType.DECL, "DECL_INIT"));
            }
        } else {
            exitList.addToken(new Token(TokenType.DECL, "DECL"));
        }

        if (cur(TokenType.SEMICOLON)) advance();
    }

    private void handleFuncDec() {
        Token returnType = advance();
        exitList.addToken(returnType);
        Token funcName = advance();
        exitList.addToken(funcName);
        advance();

        int paramCount = 0;
        while (current() != null && !cur(TokenType.RPAREN)) {
            if (cur(TokenType.COMMA)) advance();
            if (current() != null && current().isType()) {
                exitList.addToken(advance());
                exitList.addToken(advance());
                paramCount++;
            }
        }
        advance();

        exitList.addToken(new Token(TokenType.FUNC_BEGIN, paramCount, "FUNC_DECL"));

        if (cur(TokenType.LBRACE)) handleBlock();
    }

    private void handleIf() {
        advance();

        TokenList condition = extractContent();
        rpn.clear();
        exitList.addList(rpn.processArray(condition));

        exitList.addToken(new Token(TokenType.JZ, "JZ"));
        Token falseLabel = newLabel();
        exitList.addToken(falseLabel);

        if (cur(TokenType.LBRACE)) handleBlock();
        else processStatement();

        if (cur(TokenType.ELSE)) {
            Token endLabel = newLabel();
            exitList.addToken(new Token(TokenType.JMP, "JMP"));
            exitList.addToken(endLabel);
            exitList.addToken(new Token(TokenType.LABEL, falseLabel.getIndex(), "LABEL"));

            advance();
            if (cur(TokenType.LBRACE)) handleBlock();
            else processStatement();

            exitList.addToken(new Token(TokenType.LABEL, endLabel.getIndex(), "LABEL"));
        } else {
            exitList.addToken(new Token(TokenType.LABEL, falseLabel.getIndex(), "LABEL"));
        }
    }

    private void handleWhile() {
        advance();
        Token loopStart = newLabel();
        exitList.addToken(new Token(TokenType.LABEL, loopStart.getIndex(), "LABEL"));

        TokenList condition = extractContent();
        rpn.clear();
        exitList.addList(rpn.processArray(condition));

        exitList.addToken(new Token(TokenType.JZ, "JZ"));
        Token loopEnd = newLabel();
        exitList.addToken(loopEnd);
        labelStack.push(new JumpLabel(loopEnd, "WHILE", position));

        if (cur(TokenType.LBRACE)) handleBlock();
        else processStatement();

        exitList.addToken(new Token(TokenType.JMP, "JMP"));
        exitList.addToken(loopStart);

        JumpLabel endLabel = labelStack.pop();
        exitList.addToken(new Token(TokenType.LABEL, endLabel.token.getIndex(), "LABEL"));
    }

    private void handleDoWhile() {
        advance();
        Token loopStart = newLabel();
        exitList.addToken(new Token(TokenType.LABEL, loopStart.getIndex(), "LABEL"));

        if (cur(TokenType.LBRACE)) handleBlock();
        else processStatement();

        advance();
        TokenList condition = extractContent();
        rpn.clear();
        exitList.addList(rpn.processArray(condition));

        exitList.addToken(new Token(TokenType.JNZ, "JMP_IF_TRUE"));
        exitList.addToken(loopStart);

        if (cur(TokenType.SEMICOLON)) advance();
    }

    private void handleFor() {
        advance();
        advance();

        TokenList init = extractUntil(TokenType.SEMICOLON);
        if (!init.isEmpty()) { rpn.clear(); exitList.addList(rpn.processArray(init)); }
        advance();

        Token loopStart = newLabel();
        exitList.addToken(new Token(TokenType.LABEL, loopStart.getIndex(), "LABEL"));

        TokenList condition = extractUntil(TokenType.SEMICOLON);
        if (!condition.isEmpty()) { rpn.clear(); exitList.addList(rpn.processArray(condition)); }
        advance();

        exitList.addToken(new Token(TokenType.JZ, "JZ"));
        Token loopEnd = newLabel();
        exitList.addToken(loopEnd);
        labelStack.push(new JumpLabel(loopEnd, "FOR", position));

        TokenList increment = extractUntil(TokenType.RPAREN);
        advance();

        if (cur(TokenType.LBRACE)) handleBlock();
        else processStatement();

        if (!increment.isEmpty()) { rpn.clear(); exitList.addList(rpn.processArray(increment)); }

        exitList.addToken(new Token(TokenType.JMP, "JMP"));
        exitList.addToken(loopStart);

        JumpLabel endLabel = labelStack.pop();
        exitList.addToken(new Token(TokenType.LABEL, endLabel.token.getIndex(), "LABEL"));
    }

    private void handleReturn() {
        advance();
        if (current() != null && !cur(TokenType.SEMICOLON)) {
            TokenList returnValue = extractUntil(TokenType.SEMICOLON);
            rpn.clear();
            exitList.addList(rpn.processArray(returnValue));
        }
        exitList.addToken(new Token(TokenType.RETURN_VAL, "RETURN"));
        if (cur(TokenType.SEMICOLON)) advance();
    }

    private void handleBreak() {
        advance();
        exitList.addToken(new Token(TokenType.NOP, "BREAK"));
        if (cur(TokenType.SEMICOLON)) advance();
    }

    private void handleContinue() {
        advance();
        exitList.addToken(new Token(TokenType.NOP, "CONTINUE"));
        if (cur(TokenType.SEMICOLON)) advance();
    }

    private void handleCin() {
        advance();
        int varCount = 0;
        while (cur(TokenType.RSHIFT)) {
            advance();
            exitList.addToken(advance());
            varCount++;
        }
        exitList.addToken(new Token(TokenType.NOP, varCount, "CIN"));
        if (cur(TokenType.SEMICOLON)) advance();
    }

    private void handleCout() {
        advance();
        int itemCount = 0;
        while (cur(TokenType.LSHIFT)) {
            advance();
            if (current() == null) break;
            if (current().getGroup().equals("S") || is(current(), TokenType.ENDL)) {
                exitList.addToken(advance());
                itemCount++;
            } else if (current().getGroup().equals("V") && is(peek(1), TokenType.LBRACKET)) {
                exitList.addToken(advance());
                while (cur(TokenType.LBRACKET)) {
                    advance();
                    TokenList index = extractUntil(TokenType.RBRACKET);
                    rpn.clear();
                    exitList.addList(rpn.processArray(index));
                    advance();
                    exitList.addToken(new Token(TokenType.INDEX, "AEoA"));
                }
                itemCount++;
            } else {
                TokenList expr = extractUntil(TokenType.LSHIFT, TokenType.SEMICOLON);
                if (!expr.isEmpty()) {
                    rpn.clear();
                    exitList.addList(rpn.processArray(expr));
                    itemCount++;
                }
            }
        }
        exitList.addToken(new Token(TokenType.NOP, itemCount, "COUT"));
        if (cur(TokenType.SEMICOLON)) advance();
    }

    private void handleBlock() {
        advance();

        while (hasMoreTokens()) {
            Token token = current();
            if (token == null || is(token, TokenType.RBRACE)) break;

            processStatement();

            while (!labelStack.isEmpty() && labelStack.peek().type.equals("IF")) {
                if (cur(TokenType.RBRACE)) {
                    if (is(peek(1), TokenType.ELSE)) break;
                }
                if (cur(TokenType.ELSE)) break;
                JumpLabel ifLabel = labelStack.pop();
                exitList.addToken(new Token(TokenType.LABEL, ifLabel.token.getIndex(), "LABEL"));
            }
        }

        if (cur(TokenType.RBRACE)) advance();
    }

    private void handleArrayAccess() {
        exitList.addToken(advance());

        while (cur(TokenType.LBRACKET)) {
            advance();
            TokenList index = extractUntil(TokenType.RBRACKET);
            rpn.clear();
            exitList.addList(rpn.processArray(index));
            advance();
            exitList.addToken(new Token(TokenType.INDEX, "AEoA"));
        }
        if (cur(TokenType.ASSIGN)) {
            Token assign = advance();

            if (isFuncCall()) {
                handleFuncCall();
                exitList.addToken(assign);
            } else {
                TokenList rhs = extractUntil(TokenType.SEMICOLON);
                if (!rhs.isEmpty()) {
                    rpn.clear();
                    exitList.addList(rpn.processArray(rhs));
                }
                exitList.addToken(assign);
            }

            if (cur(TokenType.SEMICOLON)) advance();
        }
    }

    private void handleFuncCall() {
        exitList.addToken(advance());
        advance();

        int argCount = 0;
        while (current() != null && !cur(TokenType.RPAREN)) {
            if (cur(TokenType.COMMA)) advance();
            TokenList arg = extractUntil(TokenType.COMMA, TokenType.RPAREN);
            if (!arg.isEmpty()) {
                rpn.clear();
                exitList.addList(rpn.processArray(arg));
                argCount++;
            }
        }
        advance();
        exitList.addToken(new Token(TokenType.CALL, argCount, "CALL"));
    }

    private void handleExpression() {
        TokenList expression = extractUntil(TokenType.SEMICOLON, TokenType.RBRACE);
        if (!expression.isEmpty()) {
            rpn.clear();
            exitList.addList(rpn.processArray(expression));
        }
        if (cur(TokenType.SEMICOLON)) advance();
    }

    private TokenList extractContent() {
        advance();
        TokenList content = extractMatchingBrackets(TokenType.LPAREN, TokenType.RPAREN);
        advance();
        return content;
    }

    private TokenList extractMatchingBrackets(TokenType open, TokenType close) {
        TokenList content = new TokenList();
        int depth = 1;
        while (hasMoreTokens() && depth > 0) {
            Token token = current();
            if (is(token, open))  depth++;
            else if (is(token, close)) {
                depth--;
                if (depth == 0) break;
            }
            content.addToken(advance());
        }
        return content;
    }

    private TokenList extractUntil(TokenType... stopTypes) {
        TokenList content = new TokenList();

        while (hasMoreTokens()) {
            Token token = current();
            if (token == null) break;

            for (TokenType stop : stopTypes) {
                if (is(token, stop)) return content;
            }

            if (is(token, TokenType.LPAREN)) {
                content.addToken(advance());
                int depth = 1;
                while (hasMoreTokens() && depth > 0) {
                    Token t = current();
                    if (t == null) break;
                    if (is(t, TokenType.LPAREN))  depth++;
                    else if (is(t, TokenType.RPAREN)) depth--;
                    content.addToken(advance());
                    if (depth == 0) break;
                }
                continue;
            }

            if (is(token, TokenType.LBRACKET)) {
                content.addToken(advance());
                int depth = 1;
                while (hasMoreTokens() && depth > 0) {
                    Token t = current();
                    if (t == null) break;
                    if (is(t, TokenType.LBRACKET))  depth++;
                    else if (is(t, TokenType.RBRACKET)) depth--;
                    content.addToken(advance());
                    if (depth == 0) break;
                }
                continue;
            }

            content.addToken(advance());
        }
        return content;
    }

    private boolean hasMoreTokens() { return position < enterList.size(); }

    private Token current() {
        if (!hasMoreTokens()) return null;
        return enterList.getToken(position);
    }

    private Token peek(int offset) {
        int idx = position + offset;
        if (idx < 0 || idx >= enterList.size()) return null;
        return enterList.getToken(idx);
    }

    private Token advance() {
        return enterList.getToken(position++);
    }
}