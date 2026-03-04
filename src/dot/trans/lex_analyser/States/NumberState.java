package dot.trans.lex_analyser.States;

import dot.trans.lex_analyser.ContextBuffer;
import dot.trans.lexem.TokenType;

public class NumberState implements State{
    @Override
    public State handle(char c, ContextBuffer ctx){
        if (Character.isDigit(c) || c == '.') {ctx.append(c); return this;}
        ctx.emit(TokenType.NUMBER);
        return new StartState().handle(c,ctx);
    }
    @Override
    public void onEnd(ContextBuffer ctx) {ctx.emit(TokenType.NUMBER);}

}
