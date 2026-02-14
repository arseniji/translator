package dot.trans.rpn_converter;

import java.util.HashMap;

public class PriorityTable {
    public static HashMap<String,Integer> priorityTable = new HashMap<>(){{
        put("R4",0); // (
        put("addressOfElement",0);
        put("F",0);
        put("R8",0); // [
        put("O6",1); // =
        put("R5",1); // )
        put("R2",1); // ,
        put("R9",1); // ]
        put("O14",2); // ||
        put("O13",3); // &&
        put("O15",4); // !
        put("O9",5); // <
        put("O10",5); // >
        put("O1",6); // +
        put("O2",6); // - binary
        put("O3",7); // *
        put("O4",7); // /
        put("U2",100); // - unary
    }};

}
