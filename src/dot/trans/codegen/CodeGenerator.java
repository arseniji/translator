package dot.trans.codegen;

import dot.trans.lexem.TokenType;
import dot.trans.token_util.Token;
import dot.trans.token_util.TokenList;

import java.util.*;

public class CodeGenerator {

    private final TokenList rpn;
    private final ArrayDeque<String> stack = new ArrayDeque<>();
    private final List<String> lines       = new ArrayList<>();
    private final Map<Integer, Integer> labelToLine = new HashMap<>();

    private final Map<Integer, String> numByIndex = new HashMap<>();
    private final Map<Integer, String> strByIndex = new HashMap<>();
    private final Map<Integer, String> varByIndex = new HashMap<>();

    private int tempCounter = 0;
    private int position    = 0;
    private int indent      = 0;

    public CodeGenerator(TokenList rpn,
                         Map<String, Integer> numbers,
                         Map<String, Integer> strings,
                         Map<String, Integer> variables) {
        this.rpn = rpn;
        numbers  .forEach((k, v) -> numByIndex.put(v, k));
        strings  .forEach((k, v) -> strByIndex.put(v, k));
        variables.forEach((k, v) -> varByIndex.put(v, k));
    }

    public String generate() {
        while (position < rpn.size()) processToken(rpn.getToken(position++));
        resolveJumps();
        return String.join("\n", lines);
    }

    private void processToken(Token t) {
        switch (t.getType()) {

            case NUMBER   -> stack.push(numByIndex.getOrDefault(t.getIndex(), "?"));
            case STRING   -> stack.push("\"" + strByIndex.getOrDefault(t.getIndex(), "?") + "\"");
            case VARIABLE -> stack.push("$" + varByIndex.getOrDefault(t.getIndex(), "var" + t.getIndex()));
            case ENDL     -> stack.push("\"\\n\"");

            case PLUS, MINUS, MUL, DIV, MOD,
                 EQ, NEQ, LT, GT, LEQ, GEQ,
                 AND, OR -> {
                String r1 = stack.pop(), r2 = stack.pop();
                String tmp = newTemp();
                emit(tmp + " = " + r2 + " " + phpOp(t.getType()) + " " + r1 + ";");
                stack.push(tmp);
            }

            case UNARY_MINUS -> { String tmp = newTemp(); emit(tmp + " = -" + stack.pop() + ";"); stack.push(tmp); }
            case NOT         -> { String tmp = newTemp(); emit(tmp + " = !" + stack.pop() + ";"); stack.push(tmp); }
            case INC         -> emit(stack.pop() + "++;");
            case DEC         -> emit(stack.pop() + "--;");

            case ASSIGN -> { String val = stack.pop(); emit(stack.pop() + " = " + val + ";"); }

            case DECL -> {
                if (t.getValue().equals("DECL_INIT")) {
                    String val = stack.pop(), var = stack.pop();
                    emit(var + " = " + val + ";");
                } else {
                    emit(stack.pop() + " = null;");
                }
            }

            case ALLOC -> { String size = stack.pop(); emit(stack.pop() + " = array_fill(0, " + size + ", null);"); }

            case INDEX -> { String idx = stack.pop(); stack.push(stack.pop() + "[" + idx + "]"); }

            case CALL -> {
                int argc = t.getIndex();
                String[] args = new String[argc];
                for (int i = argc - 1; i >= 0; i--) args[i] = stack.pop();
                String funcName = stack.pop().replaceFirst("^\\$", "");
                String tmp = newTemp();
                emit(tmp + " = " + funcName + "(" + String.join(", ", args) + ");");
                stack.push(tmp);
            }

            case FUNC_BEGIN -> {
                int paramCount = t.getIndex();
                String[] params = new String[paramCount];
                for (int i = paramCount - 1; i >= 0; i--) params[i] = stack.pop();
                String funcName = stack.pop().replaceFirst("^\\$", "");
                emit("function " + funcName + "(" + String.join(", ", params) + ") {");
                indent++;
            }

            case RETURN_VAL -> {
                indent--;
                if (!stack.isEmpty()) emit("return " + stack.pop() + ";");
                emit("}");
            }

            case LABEL -> {
                if (t.getValue().equals("LABEL")) labelToLine.put(t.getIndex(), lines.size());
            }

            case JZ -> {
                String cond = stack.pop();
                Token lbl = rpn.getToken(position++);
                emit("if (!(" + cond + ")) goto L" + lbl.getIndex() + ";");
            }
            case JMP -> emit("goto L" + rpn.getToken(position++).getIndex() + ";");
            case JNZ -> {
                String cond = stack.pop();
                Token lbl = rpn.getToken(position++);
                emit("if (" + cond + ") goto L" + lbl.getIndex() + ";");
            }

            case NOP -> {
                switch (t.getValue()) {
                    case "CIN" -> {
                        for (int i = 0; i < t.getIndex(); i++)
                            emit(stack.pop() + " = trim(fgets(STDIN));");
                    }
                    case "COUT" -> {
                        String[] items = new String[t.getIndex()];
                        for (int i = t.getIndex() - 1; i >= 0; i--) items[i] = stack.pop();
                        emit("echo " + String.join(" . ", items) + ";");
                    }
                    case "BREAK"    -> emit("break;");
                    case "CONTINUE" -> emit("continue;");
                }
            }

            default -> {  }
        }
    }

    private String newTemp() { return "$_t" + (++tempCounter); }

    private void emit(String line) { lines.add("    ".repeat(indent) + line); }

    private String phpOp(TokenType type) {
        return switch (type) {
            case PLUS  -> "+";  case MINUS -> "-";  case MUL -> "*";   case DIV -> "/";
            case MOD   -> "%";  case EQ    -> "=="; case NEQ -> "!=";  case LT  -> "<";
            case GT    -> ">";  case LEQ   -> "<="; case GEQ -> ">=";  case AND -> "&&";
            case OR    -> "||"; case LSHIFT -> ".";
            default    -> "?";
        };
    }

    private void resolveJumps() {
        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(labelToLine.entrySet());
        entries.sort((a, b) -> {
            int cmp = b.getValue() - a.getValue();
            return cmp != 0 ? cmp : b.getKey() - a.getKey();
        });
        for (Map.Entry<Integer, Integer> e : entries)
            lines.add(e.getValue(), "    ".repeat(indent) + "L" + e.getKey() + ":");
    }
}