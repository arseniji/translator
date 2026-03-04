package dot.trans.lex_analyser.States;

import dot.trans.lex_analyser.ContextBuffer;

public interface State {

    State handle(char c, ContextBuffer ctx);
    default void onEnd(ContextBuffer ctx){}
}
