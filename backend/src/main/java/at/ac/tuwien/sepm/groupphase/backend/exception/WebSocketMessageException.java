package at.ac.tuwien.sepm.groupphase.backend.exception;

import java.util.UUID;

/**
 * Exception that signals a problem during a websocket message with it's uuid.
 */
public class WebSocketMessageException extends RuntimeException {
    private UUID uuid;

    public WebSocketMessageException(String message, UUID uuid, Throwable cause) {
        super(message, cause);
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
