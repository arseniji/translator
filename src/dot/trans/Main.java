package dot.trans;
import dot.trans.lex_analyser.*;
import dot.trans.rpn_converter.LexifiedArrayFormatter;
import dot.trans.rpn_converter.RPNStack;

import java.io.IOException;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        try{
            Parser parser = new Parser("src/dot/trans/plusFiles/simple.cpp");
            String content = parser.parse();
            System.out.println(content);
            Tokenizer tokenizer = new Tokenizer(content);
            TokenList tokenList = tokenizer.varTokenize();
            tokenList.show();

        }
        catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}
