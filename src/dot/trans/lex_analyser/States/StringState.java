package dot.trans.lex_analyser.States;

import dot.trans.lex_analyser.ContextBuffer;
import dot.trans.lexem.TokenType;

public class StringState implements State {
    @Override
    public State handle(char c, ContextBuffer ctx) {
        if (c == '"') {ctx.emit(TokenType.STRING); return new StartState();}
        ctx.append(c);
        return this;
    }
}