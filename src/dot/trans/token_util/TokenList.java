package dot.trans.token_util;

import dot.trans.lex_analyser.TokenMapper;
import dot.trans.lexem.TokenType;

import java.util.ArrayList;
import java.util.List;

public class TokenList  {
    private List<Token> tokenList;
    public TokenList(){
        this.tokenList = new ArrayList<>();
    }
    public void add(TokenType type, String value){addToken(TokenMapper.add(type,value));}
    public void addToken(Token token){
        tokenList.add(token);
    }
    public void addList(TokenList other){
        for (int i = 0; i < other.size(); i ++){
            tokenList.add(other.getToken(i));
        }
    }
    public int size(){
        return tokenList.size();
    }

    public boolean isEmpty(){
        return tokenList.isEmpty();
    }


    public Token getToken(int i){return tokenList.get(i);}
    public void setToken(int i, Token token){tokenList.set(i,token);}

    public void clear(){
        tokenList.clear();
    }

    public void show(){
        for (Token token:tokenList) System.out.println(token);
    }
}
