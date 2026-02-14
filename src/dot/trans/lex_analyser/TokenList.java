package dot.trans.lex_analyser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TokenList  {
    private List<Token> TokenList;
    private int numberCounter = 1;
    private int stringCounter = 1;
    private int varCounter = 1;
    private int charCounter = 1;
    private int commentCounter = 1;
    private int multicommentCounter = 1;
    private int errorCounter = 1;

    public TokenList(){
        this.TokenList = new ArrayList<>();
    }
    public void add(String type, String value, Integer row, Integer col){
        int count = 0;
        switch(type){
            case "O":
                count = Integer.parseInt((Lexems.OPERATORS.get(value).substring(1)));
                if (Objects.equals(count,2))
                    if (TokenList.isEmpty())
                        type = "U";
                    else if (Objects.equals(TokenList.getLast().getType(), "O") ||
                        Objects.equals(TokenList.getLast().getType(), "R")) type = "U";
                break;
            case "W":
                count = Integer.parseInt((Lexems.KEYWORDS.get(value).substring(1)));
                break;
            case "R":
                count = Integer.parseInt((Lexems.SEPARATORS.get(value).substring(1)));
                break;
            case "C":
                Lexems.COMMENTS.put(value,"C" + commentCounter);
                count = commentCounter++;
                break;
            case "M":
                Lexems.MULTICOMMENTS.put(value,"M" + multicommentCounter);
                count = multicommentCounter++;
                break;
            case "N":
                Lexems.NUMBERS.put(value,"N" + numberCounter);
                count = numberCounter++;
                break;
            case "S":
                Lexems.STRINGS.put(value,"S" + stringCounter);
                count = stringCounter++;
                break;
            case "V":
                Lexems.VARIABLES.put(value,"V" + varCounter);
                count = varCounter++;
                break;
            case "H":
                Lexems.CHARS.put(value,"H" + charCounter);
                count = charCounter++;
                break;
            default:
                type = "E";
                count = errorCounter++;
                break;
        }
        Token token = new Token(type,count,value,row,col);
        TokenList.add(token);
    }
    public void show(){
        for (Token token:TokenList) token.showAll();
    }
}
