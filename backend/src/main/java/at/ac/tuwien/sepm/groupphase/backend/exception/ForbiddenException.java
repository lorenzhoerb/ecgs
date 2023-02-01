package at.ac.tuwien.sepm.groupphase.backend.exception;

/**
 * Exception that signals that a user isn't allowed to access the requested rescource.
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException(Throwable cause) {
        super(cause);
    }
}
