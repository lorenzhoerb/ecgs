package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@ToString
@Getter
@Setter
public class GradingSystemDto {
    private Long id;

    @Size(max = 255)
    private String name;
    @Size(max = 4095)
    private String description;

    // Both bellow should be seen as Client errors. A formula can be empty but not null.

    @NotNull(message = "Client Error")
    private Boolean isPublic;

    @NotNull(message = "Client Error")
    private String formula;
}
