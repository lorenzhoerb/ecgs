package at.ac.tuwien.sepm.groupphase.backend.exception;

import java.util.List;

public class ForbiddenListException extends ErrorListException {
    public ForbiddenListException(String messageSummary, List<String> errors) {
        super("Forbidden", messageSummary, errors);
    }

    public ForbiddenListException(String messageSummary, String error) {
        super("Forbidden", messageSummary, List.of(error));
    }
}
