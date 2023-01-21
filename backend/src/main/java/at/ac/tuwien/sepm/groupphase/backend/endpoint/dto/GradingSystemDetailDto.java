package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.With;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@With
public record GradingSystemDetailDto(
    Long id,
    @Size(max = 255, message = "name to long")
    @Size(min = 1, message = "name can't be empty")
    @NotNull(message = "name can't be empty")
    String name,
    @Size(max = 4095, message = "description to long")
    @Size(min = 1, message = "description can't be empty")
    @NotNull(message = "description can't be empty")
    String description,
    @NotNull(message = "isPublic must be given")
    Boolean isPublic,
    @NotNull(message = "isTemplate must be given")
    Boolean isTemplate,
    @Size(max = 65535, message = "formula to long")
    @NotNull(message = "formula must be given")
    String formula
) {
    public GradingSystemDetailDto(
        String name, String description, Boolean isPublic, Boolean isTemplate, String formula
    ) {
        this(null, name, description, isPublic, isTemplate, formula);
    }
}
