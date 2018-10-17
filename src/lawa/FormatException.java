package lawa;

import java.text.ParseException;

public class FormatException extends RuntimeException {
    public FormatException(String s) {
        super(s);
    }

    public FormatException(ParseException ex) {
        super(ex);
    }
}
