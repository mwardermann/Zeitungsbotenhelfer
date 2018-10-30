package lawa.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class State{
    final Automaton automaton;
    private Pattern pattern;
    private final String name;

    State(Automaton automaton, String regex, String name){
        this.automaton = automaton;
        pattern = Pattern.compile(regex);
        this.name = name;
    }

    Boolean evaluate(String line){
        Matcher matcher = pattern.matcher(line);

        if (matcher.find())
        {
            apply(matcher);
            return true;
        }
        return false;
    }

    void apply(Matcher matcher){
    }

    @Override
    public String toString() {
        return pattern.pattern();
    }

    String getName() {
        return name;
    }
}
