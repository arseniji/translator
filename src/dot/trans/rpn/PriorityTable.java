package dot.trans.rpn;

import java.util.HashMap;

public class PriorityTable {
    public static HashMap<String,Integer> priorityTable = new HashMap<>(){{
        // Скобки и специальные
        put("R4", 0);  // (
        put("R5", 1);  // )
        put("R8", 0);  // [
        put("R9", 1);  // ]
        put("R2", 1);  // ,
        put("F", 0);   // function call
        put("AEoA", 0); // array element access

        put("O6", 2);   // =
        put("O18", 2);  // +=
        put("O19", 2);  // -=
        put("O20", 2);  // *=
        put("O21", 2);  // /=
        put("O22", 2);  // %=

        put("O14", 3);  // ||

        put("O13", 4);  // &&

        put("O7", 5);   // ==
        put("O8", 5);   // !=

        put("O9", 6);   // <
        put("O10", 6);  // >
        put("O11", 6);  // <=
        put("O12", 6);  // >=


        put("O23", 7);  //
        put("O24", 7);  // >>

        put("O1", 8);   // +
        put("O2", 8);   // - (бинарный)

        put("O3", 9);   // *
        put("O4", 9);   // /
        put("O5", 9);   // %

        put("U2", 10);  // - (унарный)
        put("O15", 10); // ! (логическое НЕ)
        put("O16", 10); // ++ (префикс/постфикс)
        put("O17", 10); // -- (префикс/постфикс)

        put("O25", 11); // . (точка)
    }};
}