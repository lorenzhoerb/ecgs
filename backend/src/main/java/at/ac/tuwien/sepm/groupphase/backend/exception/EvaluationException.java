package at.ac.tuwien.sepm.groupphase.backend.exception;

/**
 * Exception that signals an error during the evaluation of a grading system like division by zero.
 */
public class EvaluationException extends RuntimeException {

    public EvaluationException() {
    }

    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EvaluationException(Throwable cause) {
        super(cause);
    }
}
