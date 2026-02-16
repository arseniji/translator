package dot.trans.rpn;

import dot.trans.lex_analyser.Token;
import dot.trans.lex_analyser.TokenList;

import java.util.ArrayDeque;
import java.util.Objects;

import static dot.trans.rpn.PriorityTable.priorityTable;

public class RPN {
    private ArrayDeque<Token> stack = new ArrayDeque<>();
    private TokenList exitString = new TokenList();

    public TokenList processArray(TokenList tokenList){
        for (int i = 0; i < tokenList.size(); i++){
            add(tokenList.getToken(i));
        }
        while (!stack.isEmpty()) {
            exitString.addToken(stack.pop());
        }

        return exitString;
    }

    private void add(Token token){
        switch (token.getType()){
            case "N":
            case "V":
            case "S":
            case "H":
                exitString.addToken(token);
                break;
            case "W":
                if (token.isBool()) exitString.addToken(token);
                break;
            case "U":
            case "O":
                processOperator(token);
                break;
            case "E":
                break;
            case "R":
                switch (token.getCount()){
                    case 4: // (
                        handleOpenSmoothBracket(token);
                        break;
                    case 5: // )
                        handleClosedSmoothBracket();
                        break;
                }
                break;
        }
    }
    private void processOperator(Token token){
        while (!stack.isEmpty()
                && token.isOperator()
                && hasHigherPrecedence(stack.peek(), token)) {
            exitString.addToken(stack.pop());
        }
        stack.push(token);
    }

    private void processSmoothBracket(){
        while (!stack.isEmpty()
                && !Objects.equals(stack.peek().getTypeCount(), "R4")) {
            exitString.addToken(stack.pop());
        }
        if (!stack.isEmpty()) {stack.pop();}
    }

    private void handleOpenSmoothBracket(Token token){
        stack.push(token);
    }

    private void handleClosedSmoothBracket(){
        processSmoothBracket();
    }

    public void clear(){
        stack.clear();
        exitString.clear();
    }


    private boolean hasHigherPrecedence(Token top, Token current){
        int pTop = priorityTable.get(top.getTypeCount());
        int pCurrent = priorityTable.get(current.getTypeCount());

        if (current.isRightAssociative()) {
            return pTop > pCurrent;
        } else {
            return pTop > pCurrent || (pTop == pCurrent && !current.isRightAssociative());
        }
    }

}
