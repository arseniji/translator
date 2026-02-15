package dot.trans.lex_analyser;

import java.util.Objects;
import java.util.Set;

import static java.lang.System.in;

public class Token{
    private String type;
    private Integer count;
    private String value;
    private Integer col;
    private Integer row;


    public Token(String type, Integer count, String value, Integer row, Integer col){
        this.type = type;
        this.count = count;
        this.value = value;
        this.col = col;
        this.row = row;
    }
    public Token(String type, Integer count){
        this.type = type;
        this.count = count;
    }

    public void showAll(){
        System.out.println(String.format("Token(%s%d, '%s', line = %d, col = %d)", type, count, value, row, col));
    }
    public String getTypeCount(){
        return String.format("%s%d",type,count);
    }
    public String getType() {
        return type;
    }

    public Integer getCount() {
        return count;
    }

    public String getValue() {
        return value;
    }

    public Integer getCol() {
        return col;
    }

    public Integer getRow() {
        return row;
    }

    public boolean isOperator(){
        return (Objects.equals(type,"O") || Objects.equals(type, "U"));
    }

    public boolean isVar(){
        return Objects.equals(type,"V");
    }

    public boolean isRightAssociative(){
        return Objects.equals(getTypeCount(), "O6") || Objects.equals(getTypeCount(), "U2");
    }

    public boolean isType(){
        if (Objects.equals(type, "W"))
            return Set.of(1, 2, 3, 4, 5, 6, 18, 21, 22, 23, 24).contains(count);
        return false;
    }

    public boolean match(String typeCount){
        return getTypeCount().equals(typeCount);
    }

    public boolean matchCount(Integer count){
        return getCount().equals(count);
    }
    public boolean matchType(String type) {
        return getType().equals(type);
    }

    @Override
    public String toString(){
        return String.format("%s%d", type,count);
    }

}