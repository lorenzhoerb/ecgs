package at.ac.tuwien.sepm.groupphase.backend.exception;

import java.util.Collections;
import java.util.List;

/**
 * Exception that gives a generic List of errors.
 */
public class ErrorListException extends RuntimeException {
    private final List<String> errors;
    private final String messageSummary;
    private final String errorListDescriptor;

    public ErrorListException(String errorListDescriptor, String messageSummary, List<String> errors) {
        super(messageSummary);
        this.errorListDescriptor = errorListDescriptor;
        this.messageSummary = messageSummary;
        this.errors = errors;
    }

    /**
     * See {@link Throwable#getMessage()} for general information about this method.
     *
     * <p>Note: this implementation produces the message
     * from the {@link #summary} and the list of {@link #errors}
     */
    @Override
    public String getMessage() {
        return "%s. %s: %s."
            .formatted(messageSummary, errorListDescriptor, String.join(", ", errors));
    }

    public String summary() {
        return messageSummary;
    }

    public List<String> errors() {
        return Collections.unmodifiableList(errors);
    }
}
