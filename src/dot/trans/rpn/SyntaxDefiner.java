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
        while (hasMoreTokens() && !isEndToken(current())) {
            System.out.println("Рассматриваем токен " + current());
            processStatement();

            while (!labelStack.isEmpty() && labelStack.peek().type.equals("IF")) {
                Token next = current();
                if (next != null && next.match("W8")) {
                    break;
                }
                JumpLabel ifLabel = labelStack.pop();
                exitList.addToken(new Token("LABEL", ifLabel.token.getCount()));
            }
        }

        while (!labelStack.isEmpty() && labelStack.peek().type.equals("IF")) {
            JumpLabel label = labelStack.pop();
            exitList.addToken(new Token("LABEL", label.token.getCount()));
        }

        if (!labelStack.isEmpty()) {
            while (!labelStack.isEmpty()) {
                JumpLabel label = labelStack.pop();
            }
        }

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
                if (token.isBool()) handleExpression();
                else if (isFuncCall()) handleFuncCall();
                else handleKeyword();
                break;
            case "V":
                if (isArrayAccess()) handleArrayAccess();
                else if (isFuncCall()) handleFuncCall();
                else handleExpression();
                break;
            case "R":
                if (token.matchCount(7)) {
                    currentNext();
                    return;
                }
                if (token.matchCount(1)) {
                    currentNext();
                    return;
                }
                handleExpression();
                break;
            case "C":
            case "M":
                currentNext();
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
        if (current() != null && current().matchType("V") && peek(1) != null && peek(1).match("R4")) {
            return true;
        }
        return peek(1) != null && current().match("W29") && peek(1) != null && peek(1).match("R4");
    }

    private boolean isVarDec(){
        if (current() == null || !current().isType()) return false;
        return peek(1) != null && peek(1).matchType("V");
    }

    private boolean isFuncDec(){
        if (current() == null || !current().isType()) return false;
        if (peek(1) == null || !peek(1).matchType("V")) return false;
        return peek(2) != null && peek(2).match("R4");
    }

    private void handleKeyword(){
        Token keyword = current();

        if (keyword.isType()) {
            if (isFuncDec()) handleFuncDec();
            else if (isVarDec()) handleVarDec();
            else currentNext();
            return;
        }

        switch (keyword.getTypeCount()){
            case "W7": // if
                handleIf();
                break;
            case "W8": // else
                handleElse();
                break;
            case "W9": // for
                handleFor();
                break;
            case "W10": // while
                handleWhile();
                break;
            case "W11": // do
                handleDoWhile();
                break;
            case "W12": // return
                handleReturn();
                break;
            case "W13": // break
                handleBreak();
                break;
            case "W14": // continue
                handleContinue();
                break;
            case "W15": // switch
                handleSwitch();
                break;
            case "W26": // cin
                handleCin();
                break;
            case "W27": // cout
                handleCout();
                break;
            default:
                currentNext();
                break;
        }
    }

    private void handleVarDec(){
        Token type = currentNext();
        exitList.addToken(type);

        Token varName = currentNext();
        exitList.addToken(varName);

        if (current() != null && current().match("O6")) { // =
            currentNext();

            if (isFuncCall()) {
                handleFuncCall();
                exitList.addToken(new Token("DECL_INIT", 1));
            } else {
                TokenList value = extractUntil("R1");
                rpn.clear();
                exitList.addList(rpn.processArray(value));
                exitList.addToken(new Token("DECL_INIT", 1));
            }
        }
        else {
            exitList.addToken(new Token("DECL", 1));
        }
        if (current() != null && current().match("R1")) currentNext();
    }

    private void handleFuncDec(){
        Token returnType = currentNext();
        exitList.addToken(returnType);
        Token funcName = currentNext();
        exitList.addToken(funcName);
        currentNext();
        int paramCount = 0;
        if (current() != null && !current().match("R5")) {
            do {
                if (current() != null && current().match("R2")) {
                    currentNext();
                }
                if (current() != null && current().isType()) {
                    Token paramType = currentNext();
                    exitList.addToken(paramType);
                    Token paramName = currentNext();
                    exitList.addToken(paramName);
                    paramCount++;
                }

            }
            while (current() != null && current().match("R2"));
        }

        currentNext();

        exitList.addToken(new Token("FUNC_DECL", paramCount));

        if (current() != null && current().match("R6")) handleBlock();
    }

    private void handleIf(){
        System.out.println("DEBUG: handleIf вызван на позиции " + position);
        currentNext();
        TokenList condition = extractContent();
        rpn.clear();
        TokenList rpnCondition = rpn.processArray(condition);
        exitList.addList(rpnCondition);

        Token falseLabel = new Token("M", labelStack.size()+1);
        exitList.addToken(falseLabel);
        labelStack.push(new JumpLabel(falseLabel,"IF", position));

        if (current().match("R6")) handleBlock();
        else processStatement();

        // НЕ закрываем метку!
    }

    private void handleElse(){
        currentNext();

        Token endIfLabel = new Token("M", labelStack.size() + 1);
        exitList.addToken(new Token("JMP", 0));
        exitList.addToken(endIfLabel);

        JumpLabel ifLabel = labelStack.pop();
        exitList.addToken(new Token("LABEL", ifLabel.token.getCount()));

        labelStack.push(new JumpLabel(endIfLabel, "IF", position)); // ← ИЗМЕНЕНО: IF вместо IF_END

        if (current().match("R6")) {
            handleBlock();
        } else if (current().match("W7")) {
            handleIf();
            return; // handleIf добавит свою метку в стек
        } else {
            processStatement();

            // НОВОЕ: После statement в else закрываем незакрытые IF
            while (!labelStack.isEmpty() && labelStack.peek().type.equals("IF")) {
                Token next = current();
                if (next != null && next.match("W8")) {
                    break;
                }
                JumpLabel label = labelStack.pop();
                exitList.addToken(new Token("LABEL", label.token.getCount()));
            }
        }

        // Закрываем метку конца else
        if (!labelStack.isEmpty() && labelStack.peek().type.equals("IF")) {
            JumpLabel endLabel = labelStack.pop();
            exitList.addToken(new Token("LABEL", endLabel.token.getCount()));
        }
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

    private void handleDoWhile() {
        currentNext();
        Token loopStart = new Token("M", labelStack.size() + 1);
        exitList.addToken(new Token("LABEL", loopStart.getCount()));
        if (current() != null && current().match("R6")) handleBlock();
        else processStatement();
        currentNext();
        TokenList condition = extractContent();
        rpn.clear();
        exitList.addList(rpn.processArray(condition));

        exitList.addToken(new Token("JMP_IF_TRUE", 0));
        exitList.addToken(loopStart);

        if (current() != null && current().match("R1")) currentNext();
    }

    private void handleFor(){
        currentNext(); // пропускаем 'for'
        currentNext(); // пропускаем '('

        // Обработка инициализации
        TokenList init = extractUntil("R1");
        if (!init.isEmpty()) {
            rpn.clear();
            exitList.addList(rpn.processArray(init));
        }
        currentNext(); // пропускаем ';'

        // Метка начала цикла
        Token loopStart = new Token("M", labelStack.size() + 1);
        exitList.addToken(new Token("LABEL", loopStart.getCount()));

        // Обработка условия
        TokenList condition = extractUntil("R1");
        if (!condition.isEmpty()) {
            rpn.clear();
            exitList.addList(rpn.processArray(condition));
        }
        currentNext(); // пропускаем ';'

        // Метка выхода из цикла
        Token loopEnd = new Token("M", labelStack.size() + 2);
        exitList.addToken(loopEnd);
        labelStack.push(new JumpLabel(loopEnd, "FOR", position));

        // Извлекаем инкремент (но не добавляем его сейчас)
        TokenList increment = extractUntil("R5");
        currentNext(); // пропускаем ')'

        // Обработка тела цикла
        if (current().match("R6")) handleBlock();
        else processStatement();

        // Теперь добавляем инкремент ПЕРЕД переходом к началу
        if (!increment.isEmpty()) {
            rpn.clear();
            exitList.addList(rpn.processArray(increment));
        }

        // Переход к началу цикла (к проверке условия)
        exitList.addToken(new Token("JMP", 0));
        exitList.addToken(loopStart);

        // Метка выхода
        JumpLabel endLabel = labelStack.pop();
        exitList.addToken(new Token("LABEL", endLabel.token.getCount()));
    }

    private void handleSwitch(){
        currentNext();
        TokenList expr = extractContent();
        rpn.clear();
        exitList.addList(rpn.processArray(expr));
        exitList.addToken(new Token("SWITCH", 0));
        currentNext();
        Token switchEnd = new Token("M", labelStack.size() + 1);
        labelStack.push(new JumpLabel(switchEnd, "SWITCH", position));
        while (current() != null && !current().match("R7")) {
            if (current().match("W16")) handleCase();
            else if (current().match("W17")) handleDefault();
            else processStatement();
        }
        currentNext();
        JumpLabel endLabel = labelStack.pop();
        exitList.addToken(new Token("LABEL", endLabel.token.getCount()));
    }

    private void handleCase() {
        currentNext();
        TokenList value = extractUntil("R3");
        rpn.clear();
        exitList.addList(rpn.processArray(value));
        currentNext();
        exitList.addToken(new Token("CASE", 0));
    }

    private void handleDefault() {
        currentNext();
        currentNext();
        exitList.addToken(new Token("DEFAULT", 0));
    }

    private void handleReturn() {
        currentNext();
        if (current() != null && !current().match("R1")) {
            TokenList returnValue = extractUntil("R1");
            rpn.clear();
            exitList.addList(rpn.processArray(returnValue));
        }
        exitList.addToken(new Token("RETURN", 0));
        if (current() != null && current().match("R1")) currentNext();
    }

    private void handleBreak() {
        currentNext();
        exitList.addToken(new Token("BREAK", 0));
        if (current() != null && current().match("R1")) currentNext();
    }

    private void handleContinue() {
        currentNext();
        exitList.addToken(new Token("CONTINUE", 0));
        if (current() != null && current().match("R1")) currentNext();
    }

    private void handleCin() {
        currentNext();
        int varCount = 0;
        while (current() != null && current().match("O24")) {
            currentNext();
            Token var = currentNext();
            exitList.addToken(var);
            varCount++;
        }
        exitList.addToken(new Token("CIN", varCount));
        if (current() != null && current().match("R1")) currentNext();
    }

    private void handleCout() {
        currentNext();
        int itemCount = 0;
        while (current() != null && current().match("O23")) {
            currentNext();
            if (current() != null) {
                if (current().getType().equals("S") || current().match("W28")) {
                    exitList.addToken(currentNext());
                    itemCount++;
                }
                else {
                    TokenList expr = extractUntil("O23", "R1");
                    if (!expr.isEmpty()) {
                        rpn.clear();
                        exitList.addList(rpn.processArray(expr));
                        itemCount++;
                    }
                }
            }
        }
        exitList.addToken(new Token("COUT", itemCount));
        if (current() != null && current().match("R1")) {
            currentNext();
        }
    }
    private void handleBlock() {
        currentNext(); // пропускаем {

        while (hasMoreTokens()) {
            Token token = current();
            if (token == null || token.match("R7")) {
                break; // нашли } или конец файла
            }

            processStatement();

            // После каждого statement внутри блока закрываем незакрытые IF
            while (!labelStack.isEmpty() && labelStack.peek().type.equals("IF")) {
                Token next = current();

                // ИСПРАВЛЕНО: если текущий токен }, смотрим на следующий
                if (next != null && next.match("R7")) {
                    Token afterBrace = peek(1);
                    if (afterBrace != null && afterBrace.match("W8")) {
                        // После } идёт else, НЕ закрываем метку
                        break;
                    }
                }

                // Если текущий токен else, не закрываем
                if (next != null && next.match("W8")) {
                    break;
                }

                // Иначе закрываем IF
                JumpLabel ifLabel = labelStack.pop();
                exitList.addToken(new Token("LABEL", ifLabel.token.getCount()));
            }
        }

        if (current() != null && current().match("R7")) {
            currentNext(); // пропускаем }
        }
    }
    private void handleArrayAccess() {
        Token arrayVar = currentNext();
        exitList.addToken(arrayVar);

        while (current().match("R8")) { // [
            currentNext(); // [

            TokenList index = extractUntil("R9"); // до ]
            rpn.clear();
            exitList.addList(rpn.processArray(index));
            currentNext();
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

            }
            while (current().match("R2")); // пока есть запятые
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

        if (current() != null && current().match("R1")) currentNext();
    }

    private TokenList extractContent() {
        currentNext();
        TokenList content = extractMatchingBrackets("R4", "R5");
        currentNext();
        return content;
    }

    private TokenList extractMatchingBrackets(String open, String close) {
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
        return content;
    }

    private TokenList extractUntil(String... stopTokens) {
        TokenList content = new TokenList();

        while (hasMoreTokens()) {
            Token token = current();
            if (token == null) break;

            for (String stop : stopTokens) {
                if (token.match(stop)) {
                    return content;
                }
            }

            if (token.match("R4")) { // (
                content.addToken(currentNext());
                int depth = 1;
                while (hasMoreTokens() && depth > 0) {
                    Token t = current();
                    if (t == null) break;
                    if (t.match("R4")) depth++;
                    else if (t.match("R5")) depth--;
                    content.addToken(currentNext());
                    if (depth == 0) break;
                }
                continue;
            }

            if (token.match("R8")) { // [
                content.addToken(currentNext());
                int depth = 1;
                while (hasMoreTokens() && depth > 0) {
                    Token t = current();
                    if (t == null) break;

                    if (t.match("R8")) depth++;
                    else if (t.match("R9")) depth--;

                    content.addToken(currentNext());

                    if (depth == 0) break;
                }
                continue;
            }

            content.addToken(currentNext());
        }
        return content;
    }


}
