package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import java.util.UUID;


public class MessageErrorDto {
    private UUID uuid;

    private MessageErrorType type;
    private String message;

    public MessageErrorDto(UUID uuid, MessageErrorType type, String message) {
        this.uuid = uuid;
        this.type = type;
        this.message = message;
    }

    public MessageErrorDto() {
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public MessageErrorType getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public MessageErrorDto withUuid(UUID uuid) {
        return new MessageErrorDto(uuid, this.type, this.message);
    }

    public MessageErrorDto withType(MessageErrorType type) {
        return new MessageErrorDto(this.uuid, type, this.message);
    }

    public MessageErrorDto withMessage(String message) {
        return new MessageErrorDto(this.uuid, this.type, message);
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setType(MessageErrorType type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public enum MessageErrorType {
        MALFORMED,
        BAD_REQUEST,
        NOT_FOUND,
        UNAUTHORIZED,
        VALIDATION,
        UNKNOWN_SERVER_ERROR
    }
}
