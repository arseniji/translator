package dot.trans;
import dot.trans.codegen.CodeGenerator;
import dot.trans.lex_analyser.*;
import dot.trans.lexem.Lexems;
import dot.trans.rpn.SyntaxDefiner;
import dot.trans.token_util.TokenList;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;


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
            tokenList.showForOtchet();
            System.out.println();
            System.out.println("NUMBERS" + Lexems.formatMapWithPrefix(Lexems.NUMBERS, "N"));
            System.out.println("COMMENTS" + Lexems.formatMapWithPrefix(Lexems.COMMENTS, "C"));
            System.out.println("MULTICOM" + Lexems.formatMapWithPrefix(Lexems.MULTICOMMENTS, "M"));
            System.out.println("STRINGS" + Lexems.formatMapWithPrefix(Lexems.STRINGS, "S"));
            System.out.println("VAR" + Lexems.formatMapWithPrefix(Lexems.VARIABLES, "V"));
            System.out.println();
            System.out.println();
            SyntaxDefiner sd = new SyntaxDefiner(tokenList);
            TokenList tkl = sd.processList();
            System.out.println();
            tkl.show();
            tkl.showForOtchet();
            CodeGenerator cg = new CodeGenerator(
                    tkl,
                    Lexems.NUMBERS,
                    Lexems.STRINGS,
                    Lexems.VARIABLES
            );
            System.out.println();
            System.out.println("<?php");
            System.out.println(cg.generate());
            System.out.println("?>");
        }
        catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}
