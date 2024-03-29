package at.ac.tuwien.sepm.groupphase.backend.exception.applicationuser;

/**
 * Exception that signals, that an ApplicationUser wasn't found in the database.
 */
public class ApplicationUserNotFound extends RuntimeException {
    public ApplicationUserNotFound() {
    }

    public ApplicationUserNotFound(String message) {
        super(message);
    }

    public ApplicationUserNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationUserNotFound(Exception e) {
        super(e);
    }
}
