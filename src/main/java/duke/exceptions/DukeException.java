package duke.exceptions;

/** Class to represent the exceptions encountered for Duke. */
public class DukeException extends Exception {
    public DukeException(String errorMsg) {
        super(" OOPS!!! " + errorMsg);
    }
}
