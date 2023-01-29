package at.ac.tuwien.sepm.groupphase.backend.exception;

public class ConstraintParserException extends Exception {
    public ConstraintParserException() {
    }

    public ConstraintParserException(String message) {
        super(message);
    }

    public ConstraintParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstraintParserException(Throwable cause) {
        super(cause);
    }
}
