package dot.trans.lex_analyser;

import dot.trans.lexem.Lexems;
import dot.trans.lexem.TokenType;
import dot.trans.token_util.TokenList;

public class ContextBuffer {
    private StringBuilder buffer = new StringBuilder();
    private TokenList tokenList;

    public ContextBuffer(TokenList tokenList){
        this.tokenList = tokenList;
    }

    public void append(char c){ buffer.append(c);}

    public int getBufferLength() {return buffer.length();}

    public void setBufferLength(int size) {buffer.setLength(size);}

    public String getBufferString() {return buffer.toString();}

    public void emit(TokenType type){
        if (!buffer.isEmpty()){
            tokenList.add(type,buffer.toString());
            buffer.setLength(0);
        }
    }

    public void emitSingle(TokenType type, char c){
        tokenList.add(type,String.valueOf(c));
    }

    public void emitWord() {
        if (!buffer.isEmpty()) {
            String s = buffer.toString();
            buffer.setLength(0);
            TokenType type = Lexems.KEYWORDS.getOrDefault(s, TokenType.VARIABLE);
            tokenList.add(type, s);
        }
    }

    public void add(TokenType type, String value){
        tokenList.add(type,value);
    }

}
