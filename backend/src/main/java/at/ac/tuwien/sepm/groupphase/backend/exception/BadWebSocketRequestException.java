package at.ac.tuwien.sepm.groupphase.backend.exception;

public class BadWebSocketRequestException extends RuntimeException {
    public BadWebSocketRequestException() {
    }

    public BadWebSocketRequestException(String message) {
        super(message);
    }

    public BadWebSocketRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadWebSocketRequestException(Throwable cause) {
        super(cause);
    }

    public BadWebSocketRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
