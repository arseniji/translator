package dot.trans.lex_analyser.States;

import dot.trans.lex_analyser.ContextBuffer;
import dot.trans.lexem.Lexems;
import dot.trans.lexem.TokenType;

public class StartState implements State{
    @Override
    public State handle (char c, ContextBuffer ctx){
        if (Character.isWhitespace(c)){return this;}
        if (Character.isDigit(c)){ctx.append(c);return new NumberState();}
        if (Character.isLetter(c) || c == '_'){ctx.append(c);return new WordState();}
        if (c == '"'){return new StringState();}
        if (c == '\''){return new CharState();}
        if (c == '/'){return new SlashState();}
        if (Lexems.SEPARATORS.containsKey(String.valueOf(c))) {ctx.emitSingle(Lexems.SEPARATORS.get(String.valueOf(c)), c);return this;}
        if (Lexems.OPERATORS.containsKey(String.valueOf(c)) || c == '|' || c == '&'){return new OperatorState(c);}
        ctx.emitSingle(TokenType.ERROR,c);
        return this;
    }
}
