package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record SimpleFlagDto(
    @NotNull(message = "id must be given")
    //@Notice: Flag id can be negative. This signals a new flag
    Long id,
    @NotNull(message = "Flag must be specified")
    @NotBlank(message = "Flag must be specified")
    @Size(max = 255, message = "Flag is too long")
    String name
) {

}
