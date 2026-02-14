package dot.trans.rpn_converter;

import dot.trans.lex_analyser.Lexems;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;

import static dot.trans.rpn_converter.PriorityTable.priorityTable;

public class RPNStack {
    private ArrayDeque<String> stack = new ArrayDeque<>();
    private List<String> exitString = new ArrayList<>();
    private ArrayDeque<Integer> countersAOE = new ArrayDeque<>();
    private Set<String> tokenFunctions = Set.of("F","AOE");

    public List<String> convertToRPN(List<String> tokens){
        for (String token : tokens) {
            show(token);
            if (isOperand(token)) {
                exitString.add(token);
            }
            else if (isOperator(token)) {
                processOperator(token);
            }
            else if (token.equals("R4")) { // (
                if (exitString.getLast().startsWith("V")) processF();
                stack.push(token);
            }
            else if (token.equals("R5")) { // )
                if (Objects.equals(checkStackForKeys(),"F")){
                    processRightSmoothBracket();
                    exitString.add(exitString.size()-1,(countersAOE.pop()).toString());
                }
                else processRightSmoothBracket();
            }
            else if (token.equals("R8")){ // [
                stack.push(token);
                processAOE();
            }
            else if (token.equals("R2")){ // ,
                String key = checkStackForKeys();
                processCommaOperand(key);
            }

            else if (token.equals("R9")) { // ]
                processRightSharpBracket();
                exitString.add(exitString.size()-1,(countersAOE.pop()).toString());
            }
            else if (token.equals("EOF")) {
                break;
            }
        }

        while (!stack.isEmpty()) {
            exitString.add(stack.pop());
        }
        return exitString;
    }
    private void processOperator(String token) {
        while (!stack.isEmpty()
                && isOperator(stack.peek())
                && hasHigherPrecedence(stack.peek(), token)) {
            exitString.add(stack.pop());
        }
        stack.push(token);
    }

    private void processRightSmoothBracket() {
        while (!stack.isEmpty()
                && !stack.peek().equals("R4")) {
            exitString.add(stack.pop());
        }
        if (!stack.isEmpty()) {stack.pop();}
    }

    private void processComma(String arg){
        while (!stack.isEmpty() && !stack.peek().equals(arg)) {exitString.add(stack.pop());}
    }

    private void processRightSharpBracket(){
        while (!stack.isEmpty() && !stack.peek().equals("R8")) {exitString.add(stack.pop());}
        if (!stack.isEmpty()) {stack.pop();}
    }

    private void processAOE(){
        countersAOE.push(2);
        stack.push("AOE");
    }

    private void processCommaOperand(String identifier){
        countersAOE.push(countersAOE.pop()+1);
        processComma(identifier);
    }

    private void processF(){
        countersAOE.push(2);
        stack.push("F");
    }
    private String checkStackForKeys(){
        Optional<String> out = stack.stream().filter(tokenFunctions::contains).findFirst();
        return out.orElse(null);
    }

    private boolean isOperand(String token) {
        return token.startsWith("N") || token.startsWith("V");
    }
    private boolean isOperator(String token) {
        return token.startsWith("O");
    }
    private boolean isRightAssociative(String token) { return token.equals("O6") || token.equals("O2U"); }

    private boolean hasHigherPrecedence(String top, String current) {
        int pTop = priorityTable.get(top);
        int pCurrent = priorityTable.get(current);

        if (isRightAssociative(current)) {
            return pTop > pCurrent;
        } else {
            return pTop > pCurrent || (pTop == pCurrent && !isRightAssociative(current));
        }
    }

    private void show(String token){
        int i = 0;
        System.out.println("current element: " + token);
        System.out.println("STACK: ");
        for (String item : stack) {
            System.out.println(String.format("%d: %s", i++, item));
        }
        System.out.println("EXITSTRING: " + exitString);
        System.out.println();
    }
}
