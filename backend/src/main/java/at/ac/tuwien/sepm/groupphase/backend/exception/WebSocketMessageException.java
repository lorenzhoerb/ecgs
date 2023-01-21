package at.ac.tuwien.sepm.groupphase.backend.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class WebSocketMessageException extends RuntimeException {
    private UUID uuid;

    public WebSocketMessageException(String message, UUID uuid, Throwable cause) {
        super(message, cause);
        this.uuid = uuid;
    }

}
