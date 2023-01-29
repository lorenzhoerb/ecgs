package at.ac.tuwien.sepm.groupphase.backend.exception;

public class ConstraintException extends Exception {

    public ConstraintException(String message) {
        super(message);
    }

    public ConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstraintException(Throwable cause) {
        super(cause);
    }
}
