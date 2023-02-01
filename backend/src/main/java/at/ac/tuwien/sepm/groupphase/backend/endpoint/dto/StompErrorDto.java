package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.With;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public class StompErrorDto {
    private StompErrorType type;
    private String message;

    public StompErrorDto(StompErrorType type, String message) {
        this.type = type;
        this.message = message;
    }

    public StompErrorDto() {
    }

    public StompErrorType getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public StompErrorDto withType(StompErrorType type) {
        return this.type == type ? this : new StompErrorDto(type, this.message);
    }

    public StompErrorDto withMessage(String message) {
        return this.message == message ? this : new StompErrorDto(this.type, message);
    }

    public void setType(StompErrorType type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public enum StompErrorType {
        Unauthorized
    }
}
