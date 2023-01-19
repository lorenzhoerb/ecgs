package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.With;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@With
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StompErrorDto {
    private StompErrorType type;
    private String message;

    public enum StompErrorType {
        Unauthorized
    }
}
