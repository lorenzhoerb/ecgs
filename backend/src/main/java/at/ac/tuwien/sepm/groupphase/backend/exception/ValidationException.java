package at.ac.tuwien.sepm.groupphase.backend.exception;

/**
 * Exception that signals that the given resources is malformed or doesn't apply to the servers requirements.
 */
public class ValidationException extends RuntimeException {

    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }
}
