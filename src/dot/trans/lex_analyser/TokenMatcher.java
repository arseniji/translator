package dot.trans.lex_analyser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TokenMatcher {
    public String type;
    public Pattern pattern;
    public boolean keep;
    public Matcher matcher;
    public String input;

    public TokenMatcher(String type, String regex, boolean keep){
        this.type = type;
        this.pattern = Pattern.compile("^(" + regex + ")");
        this.keep = keep;
    }

    public TokenMatcher(String type, String regex, boolean keep, int flags){
        this.type = type;
        this.pattern = Pattern.compile("^(" + regex + ")", flags);
        this.keep = keep;
    }

    public void initMatcher(String input){
        this.input = input;
        this.matcher = pattern.matcher(input);
    }

    public boolean lookingAt(int pos) {
        this.matcher.region(pos, input.length());
        if (matcher.lookingAt()) {
            return matcher.start() == pos && matcher.end() > pos;
        }
        return false;
    }

    public String getMatchedText(){
        return matcher.group();
    }

    public int getMatchedEnd(){
        return matcher.end();
    }
}
