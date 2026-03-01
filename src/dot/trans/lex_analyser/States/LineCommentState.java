package dot.trans.lex_analyser.States;

import dot.trans.lex_analyser.ContextBuffer;
import dot.trans.lex_analyser.TokenList;
import dot.trans.lex_analyser.lexem.TokenType;

public class LineCommentState implements State{
    @Override
    public State handle(char c, ContextBuffer ctx){
        if (c == '\n') {
            ctx.emit(TokenType.COMMENT); return new StartState();}
        ctx.append(c);
        return this;
    }

    @Override
    public void onEnd(ContextBuffer ctx) {ctx.emit(TokenType.COMMENT);}
}
