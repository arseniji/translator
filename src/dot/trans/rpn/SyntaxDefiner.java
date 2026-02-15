package dot.trans.rpn;

import dot.trans.lex_analyser.Token;
import dot.trans.lex_analyser.TokenList;

import java.util.ArrayDeque;
import java.util.Objects;

public class SyntaxDefiner {
    private TokenList enterList;
    private TokenList builderList = new TokenList();
    private TokenList exitList = new TokenList();
    private ArrayDeque<Token> mark = new ArrayDeque<>();
    private RPN rpn = new RPN();

    public SyntaxDefiner(TokenList enterList){
        this.enterList = enterList;
    }

    public TokenList processList(){
        while (!Objects.equals(enterList.peekFirst().getType(), "E")) processToken(enterList.popFirst());
        return exitList;
    }

    private void processToken(Token token){
        switch (token.getType()){
            case "R":
                switch (token.getCount()){
                    case 8: // [
                        buildAEoA();
                        break;
                    case 4: // (
                        switch (builderList.peekLast().getTypeCount()){
                            case "W7": // if
                                buildIf();
                                break;
                            case "W9": // for
                                buildFor();
                                break;
                            case "W10": // while
                                buildWhile();
                                break;
                            default:
                                buildF();
                                break;
                        }
                }
        }
        builderList.addToken(token);
    }

    private void buildAEoA(){
        while (!Objects.equals(enterList.peekFirst().getTypeCount(), "R9")){
            builderList.addToken(enterList.popFirst());
        }
        enterList.popFirst();
        rpn.clear();
        exitList.addList(rpn.processArray(builderList));
        exitList.addToken(new Token("AEoA",2));
        builderList.clear();
    }

    private void buildIf(){
        int bracketCount = 1;
        while (!Objects.equals(enterList.peekFirst().getTypeCount(), "R5") && bracketCount%2 == 1){
            if (Objects.equals(enterList.peekFirst().getTypeCount(), "R4")) bracketCount++;
            if (Objects.equals(enterList.peekFirst().getTypeCount(), "R5")) bracketCount--;
            builderList.addToken(enterList.popFirst());
        }
        enterList.popFirst();
        rpn.clear();
        exitList.addList(rpn.processArray(builderList));
        Token token = new Token("M",1);
        exitList.addToken(token);
        mark.push(token);
        builderList.clear();
    }

    private void buildFor(){}

    private void buildWhile(){}

    private void buildF(){
        int count = 1;
        boolean flag = false;
        while (!flag){ // )
            while (!Objects.equals(enterList.peekFirst().getTypeCount(), "R2")){ // ,
                if (Objects.equals(enterList.peekFirst().getTypeCount(), "R5")) { flag = true; break;}
                builderList.addToken(enterList.popFirst());
            }
            enterList.popFirst();
            rpn.clear();
            exitList.addList(rpn.processArray(builderList));
            count++;
            builderList.clear();
        }
        exitList.addToken(new Token("F",count));
    }

}
