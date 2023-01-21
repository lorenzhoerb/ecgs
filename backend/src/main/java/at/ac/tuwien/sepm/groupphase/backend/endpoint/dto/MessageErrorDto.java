package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import java.util.UUID;

@With
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageErrorDto {
    private UUID uuid;

    private MessageErrorType type;
    private String message;

    public enum MessageErrorType {
        MALFORMED,
        BAD_REQUEST,
        NOT_FOUND,
        UNAUTHORIZED,
        VALIDATION,
        UNKNOWN_SERVER_ERROR
    }
}
