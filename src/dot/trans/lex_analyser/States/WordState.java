package dot.trans.lex_analyser.States;

import dot.trans.lex_analyser.ContextBuffer;

public class WordState implements State{
    @Override
    public State handle(char c, ContextBuffer ctx){
        if (Character.isLetter(c) || c == '_' || Character.isDigit(c)){ctx.append(c); return this;}
        ctx.emitWord();
        return new StartState().handle(c,ctx);
    }
    @Override
    public void onEnd(ContextBuffer ctx) {ctx.emitWord();}
}
