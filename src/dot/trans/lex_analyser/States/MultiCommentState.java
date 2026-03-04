package dot.trans.lex_analyser.States;

import dot.trans.lex_analyser.ContextBuffer;
import dot.trans.lexem.TokenType;

public class MultiCommentState implements State{
    private boolean lastIsStar;
    @Override
    public State handle(char c, ContextBuffer ctx){
        if (lastIsStar && c == '/') {ctx.emit(TokenType.MULTICOMMENT); return new StartState();}
        lastIsStar = (c == '*');
        if (!lastIsStar) ctx.append(c);
        return this;
    }
}
