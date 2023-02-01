package at.ac.tuwien.sepm.groupphase.backend.exception;

/**
 * Exception that signals the strategy on a variable in a grading system was violated by the bound values.
 */
public class StrategyException extends RuntimeException {

    public StrategyException() {
    }

    public StrategyException(String message) {
        super(message);
    }

    public StrategyException(String message, Throwable cause) {
        super(message, cause);
    }

    public StrategyException(Exception e) {
        super(e);
    }
}
