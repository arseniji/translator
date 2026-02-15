package dot.trans.rpn;

import dot.trans.lex_analyser.Token;
import dot.trans.lex_analyser.TokenList;

import java.util.ArrayDeque;
import java.util.Objects;

public class SyntaxDefiner {
    private TokenList enterList;
    private TokenList exitList = new TokenList();
    private ArrayDeque<JumpLabel> labelStack = new ArrayDeque<>();
    private RPN rpn = new RPN();
    private int position = 0;

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

    public SyntaxDefiner(TokenList enterList){
        this.enterList = enterList;
    }

    public TokenList processList(){
        while (hasMoreTokens() && !isEndToken(current())) processStatement();
        return exitList;
    }

    private boolean hasMoreTokens() {return position < enterList.size();}
    private boolean isEndToken(Token token){return token == null || token.getType().equals("E");}

    private Token current(){
        if (!hasMoreTokens()) return null;
        return enterList.getToken(position);
    }

    private Token peek(int offset){
        return enterList.getToken(position + offset);
    }

    private Token currentNext(){
        return enterList.getToken(position++);
    }



    private void processStatement(){
        Token token = current();
        if (token == null) return;

        switch (token.getType()){
            case "W": // ключевое слово
                handleKeyword();
                break;
            case "V":
                if (isArrayAccess()) handleArrayAccess();
                else if (isFuncCall()) handleFuncCall();
                else handleExpression();
                break;
            case "R":
                if (token.matchCount(7)) return;
                handleExpression();
                break;
            default:
                handleExpression();
                break;
        }
    }

    private boolean isArrayAccess(){
        Token next = peek(1);
        return next != null && next.match("R8");
    }

    private boolean isFuncCall(){
        Token next = peek(1);
        return next != null && next.match("R4");
    }

    private boolean isAssignment(){
        int forward = 1;
        while (hasMoreTokens()){
            Token token = peek(forward);
            if (token == null) return false;
            if (token.match("O6")) return true; // =
            if (token.match("R1") || token.match("R7")) return false; // ; или }
            forward++;
        }
        return false;
    }

    private void handleKeyword(){
        Token keyword = current();
        switch (keyword.getTypeCount()){
            case "W7": // if
                handleIf();
                break;
            case "W9": // for
                handleFor();
                break;
            case "W10": // while
                handleWhile();
                break;
            case "W8": // else
                handleElse();
                break;
            default:
                currentNext();
                break;
        }
    }

    private void handleIf(){
        currentNext();
        TokenList condition = extractContent();
        rpn.clear();
        TokenList rpnCondition = rpn.processArray(condition);
        exitList.addList(rpnCondition);

        Token falseLabel = new Token("M", labelStack.size()+1);
        exitList.addToken(falseLabel);
        labelStack.push(new JumpLabel(falseLabel,"IF", position));

        if (current().match("R6")){handleBlock();}
        else processStatement();
    }

    private void handleElse(){
        currentNext();
        Token endIfLabel = new Token("M", labelStack.size() + 1);
        exitList.addToken(new Token("JMP", 0));
        exitList.addToken(endIfLabel);
        JumpLabel ifLabel = labelStack.pop();
        exitList.addToken(new Token("LABEL", ifLabel.token.getCount()));

        if (current().match("R6")) {handleBlock();}
        else if (current().match("W7")) {handleIf();}
        else processStatement();

        exitList.addToken(new Token("LABEL", endIfLabel.getCount()));
    }

    private void handleWhile(){
        currentNext();
        Token loopStart = new Token("M", labelStack.size() + 1);
        exitList.addToken(new Token("LABEL", loopStart.getCount()));

        TokenList condition = extractContent();
        rpn.clear();
        TokenList rpnCondition = rpn.processArray(condition);
        exitList.addList(rpnCondition);

        Token loopEnd = new Token("M", labelStack.size() + 2);
        exitList.addToken(loopEnd);
        labelStack.push(new JumpLabel(loopEnd, "WHILE", position));

        if (current().match("R6")) handleBlock();
        else processStatement();

        exitList.addToken(new Token("JMP", 0));
        exitList.addToken(loopStart);

        JumpLabel endLabel = labelStack.pop();
        exitList.addToken(new Token("LABEL", endLabel.token.getCount()));
    }

    private void handleFor(){
        currentNext();
        TokenList init = extractUntil("R1");
        if (!init.isEmpty()) {
            rpn.clear();
            exitList.addList(rpn.processArray(init));
        }
        Token loop = new Token("M", labelStack.size() + 1);
        exitList.addToken(new Token("LABEL", loop.getCount()));

        TokenList condition = extractUntil("R1");
        if (!condition.isEmpty()) {
            rpn.clear();
            exitList.addList(rpn.processArray(condition));
        }

        Token loopEnd = new Token("M", labelStack.size() + 2);
        exitList.addToken(loopEnd);
        labelStack.push(new JumpLabel(loopEnd, "FOR", position));

        TokenList increment = extractUntil("R5");
        if (current().match("R6")) handleBlock();
        else processStatement();

        if (!increment.isEmpty()) {
            rpn.clear();
            exitList.addList(rpn.processArray(increment));
        }

        exitList.addToken(new Token("JMP", 0));
        exitList.addToken(loop);

        JumpLabel endLabel = labelStack.pop();
        exitList.addToken(new Token("LABEL", endLabel.token.getCount()));
    }

    private void handleBlock() {
        currentNext();
        while (!current().match("R7") && hasMoreTokens())  processStatement(); // пока не }
        currentNext();
    }

    private void handleArrayAccess() {
        Token arrayVar = currentNext();
        exitList.addToken(arrayVar);

        while (current().match("R8")) { // [
            currentNext(); // [

            TokenList index = extractUntil("R9"); // до ]
            rpn.clear();
            TokenList rpnIndex = rpn.processArray(index);
            exitList.addList(rpnIndex);

            exitList.addToken(new Token("AEoA", 1)); // Операция доступа к элементу
        }
    }

    private void handleFuncCall() {
        Token functionName = currentNext();
        exitList.addToken(functionName);

        currentNext();

        int argCount = 0;
        if (!current().match("R5")) {
            do {
                if (current().match("R2")) currentNext(); // ,
                TokenList arg = extractUntil("R2", "R5"); // до , или )
                if (!arg.isEmpty()) {
                    rpn.clear();
                    exitList.addList(rpn.processArray(arg));
                    argCount++;
                }

            } while (current().match("R2")); // пока есть запятые
        }

        currentNext();

        exitList.addToken(new Token("F", argCount));
    }

    private void handleExpression() {
        TokenList expression = extractUntil("R1", "R7", "E1"); //  ; } EOF

        if (!expression.isEmpty()) {
            rpn.clear();
            exitList.addList(rpn.processArray(expression));
        }

        if (current().match("R1")) currentNext();
    }

    private TokenList extractContent() {
        return extractMatchingBrackets("R4", "R5");
    }

    private TokenList extractMatchingBrackets(String open, String close) {
        currentNext();
        TokenList content = new TokenList();
        int depth = 1;
        while (hasMoreTokens() && depth > 0) {
            Token token = current();
            if (token.match(open)) {
                depth++;
            } else if (token.match(close)) {
                depth--;
                if (depth == 0) break;
            }
            content.addToken(currentNext());
        }
        currentNext();
        return content;
    }

    private TokenList extractUntil(String... stopTokens) {
        TokenList content = new TokenList();

        while (hasMoreTokens()) {
            Token token = current();

            for (String stop : stopTokens) {
                if (token.match(stop)) {
                    return content;
                }
            }

            if (token.match("R4")) { // (
                content.addToken(currentNext());
                content.addList(extractMatchingBrackets("R4", "R5"));
                continue;
            }

            if (token.match("R8")) { // [
                content.addToken(currentNext());
                content.addList(extractMatchingBrackets("R8", "R9"));
                continue;
            }

            content.addToken(currentNext());
        }
        return content;
    }


}
