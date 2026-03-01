package dot.trans.lex_analyser.States;

import dot.trans.lex_analyser.ContextBuffer;
import dot.trans.lex_analyser.lexem.Lexems;
import dot.trans.lex_analyser.TokenList;
import dot.trans.lex_analyser.lexem.TokenType;

public class SlashState implements State{
    @Override
    public State handle(char c, ContextBuffer ctx){
        if (c == '/') return new LineCommentState();
        if (c == '*') return new MultiCommentState();
        if (c == '=') {ctx.add(TokenType.DIV_ASSIGN,"/="); return new StartState();}
        ctx.add(Lexems.OPERATORS.get("/"), "/");
        return new StartState().handle(c,ctx);
    }

    @Override
    public void onEnd(ContextBuffer ctx ) {ctx.add(Lexems.OPERATORS.get("/"),"/");}
}
