package dot.trans;
import dot.trans.lex_analyser.*;
import dot.trans.rpn.RPN;
import dot.trans.rpn.SyntaxDefiner;
import dot.trans.token_util.TokenList;
//import dot.trans.rpn.RPN;
//import dot.trans.rpn.SyntaxDefiner;

import java.io.IOException;
import java.util.Arrays;


public class Main {
    public static void main(String[] args) {
        try{
            Parser parser = new Parser("src/dot/trans/plusFiles/simple.cpp");
            String content = parser.parse();
            System.out.println(content);
            Tokenizer tokenizer = new Tokenizer(content);
            TokenList tokenList = tokenizer.varTokenize();
            tokenList = UnaryResolver.resolve(tokenList);
            tokenList.show();
            System.out.println();
            System.out.println();
            SyntaxDefiner sd = new SyntaxDefiner(tokenList);
            TokenList tkl = sd.processList();
            System.out.println();
            tkl.show();
        }
        catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}
