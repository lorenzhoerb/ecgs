package at.ac.tuwien.sepm.groupphase.backend.exception;

import java.util.List;

/**
 * List of Exceptions that signals that a user isn't allowed to access the requested rescource.
 */
public class ForbiddenListException extends ErrorListException {
    public ForbiddenListException(String messageSummary, List<String> errors) {
        super("Forbidden", messageSummary, errors);
    }

    public ForbiddenListException(String messageSummary, String error) {
        super("Forbidden", messageSummary, List.of(error));
    }
}
