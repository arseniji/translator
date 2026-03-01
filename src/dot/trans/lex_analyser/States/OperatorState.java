package dot.trans.lex_analyser.States;

import dot.trans.lex_analyser.ContextBuffer;
import dot.trans.lex_analyser.lexem.Lexems;
import dot.trans.lex_analyser.TokenList;

public class OperatorState implements State{
    private Character firstChar;

    public OperatorState(char first){
        this.firstChar = first;
    }

    @Override
    public State handle(char c, ContextBuffer ctx){
        int savedLength = ctx.getBufferLength();
        ctx.append(firstChar);
        ctx.append(c);
        if (Lexems.OPERATORS.containsKey(ctx.getBufferString())){
            ctx.emit(Lexems.OPERATORS.get(ctx.getBufferString()));
            return new StartState();
        }
        ctx.setBufferLength(savedLength);
        ctx.emitSingle(Lexems.OPERATORS.get(String.valueOf(firstChar)),firstChar);
        return new StartState().handle(c,ctx);
    }
    @Override
    public void onEnd(ContextBuffer ctx){
        ctx.emitSingle(Lexems.OPERATORS.get(String.valueOf(firstChar)),firstChar);
    }
}
