package dot.trans.lex_analyser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Parser {
    private String path;

    public Parser(String path){
        this.path = path;
        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            System.err.println("Предупреждение: файл не найден по пути: " + path);
            System.err.println("Абсолютный путь: " + filePath.toAbsolutePath());
        }
    }

    public String parse() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(path)));
        return content;
    }
}
