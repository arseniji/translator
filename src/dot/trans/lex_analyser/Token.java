package dot.trans.lex_analyser;

public class Token{
    public String type;
    public Integer count;
    public String value;
    public Integer col;
    public Integer row;

    public Token(String type, Integer count, String value, Integer row, Integer col){
        this.type = type;
        this.count = count;
        this.value = value;
        this.col = col;
        this.row = row;
    }
    public void showAll(){
        System.out.println(String.format("Token(%s%d, '%s', line = %d, col = %d)", type, count, value, row, col));
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

    @Override
    public String toString(){
        return String.format("%s%d", type,count);
    }

}