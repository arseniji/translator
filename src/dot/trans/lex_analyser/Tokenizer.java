package dot.trans.lex_analyser;

import dot.trans.lex_analyser.States.StartState;
import dot.trans.lex_analyser.States.State;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private String input;
    private TokenList tokenList;
    private ContextBuffer buffer;
    private State state;

    public Tokenizer(String input) {
        this.input = input;
        this.tokenList = new TokenList();
        this.buffer = new ContextBuffer(tokenList);
        this.state = new StartState();
    }

    public TokenList varTokenize() {
        for (char c: input.toCharArray()){
            state = state.handle(c,buffer);
        }
        state.onEnd(buffer);
        return tokenList;
    }
}
