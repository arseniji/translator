package dot.trans.rpn_converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LexifiedArrayFormatter {
    static List<String> output = new ArrayList<>();
    public static List<String> format(List<String> array){
        for (String item:array){
            if (Objects.equals(item,"O2")){
                if (!output.isEmpty()){
                    if (output.getLast().startsWith("O") || output.getLast().startsWith("R")) output.add("O2U");
                    else output.add("O2");
                }
                else output.add("O2U");
            }
            else output.add(item);
        }
        return output;
    }
}
