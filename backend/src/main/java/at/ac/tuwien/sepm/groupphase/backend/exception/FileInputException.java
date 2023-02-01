package at.ac.tuwien.sepm.groupphase.backend.exception;

/**
 * Exception that signals that the file sent to the server was incorrect or malformed.
 */
public class FileInputException extends RuntimeException {

    public FileInputException() {
    }

    public FileInputException(String message) {
        super(message);
    }

    public FileInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileInputException(Throwable cause) {
        super(cause);
    }
}
