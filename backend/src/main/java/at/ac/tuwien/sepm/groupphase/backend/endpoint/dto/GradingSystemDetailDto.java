package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    public GradingSystemDetailDto withId(Long id) {
        return this.id == id ? this : new GradingSystemDetailDto(id, this.name, this.description, this.isPublic, this.isTemplate, this.formula);
    }

    public GradingSystemDetailDto withName(
        @Size(max = 255, message = "name to long") @Size(min = 1, message = "name can't be empty") @NotNull(message = "name can't be empty") String name) {
        return this.name == name ? this : new GradingSystemDetailDto(this.id, name, this.description, this.isPublic, this.isTemplate, this.formula);
    }

    public GradingSystemDetailDto withDescription(
        @Size(max = 4095, message = "description to long") @Size(min = 1, message = "description can't be empty") @NotNull(message = "description can't be empty") String description) {
        return this.description == description ? this :
            new GradingSystemDetailDto(this.id, this.name, description, this.isPublic, this.isTemplate, this.formula);
    }

    public GradingSystemDetailDto withIsPublic(@NotNull(message = "isPublic must be given") Boolean isPublic) {
        return this.isPublic == isPublic ? this : new GradingSystemDetailDto(this.id, this.name, this.description, isPublic, this.isTemplate, this.formula);
    }

    public GradingSystemDetailDto withIsTemplate(@NotNull(message = "isTemplate must be given") Boolean isTemplate) {
        return this.isTemplate == isTemplate ? this : new GradingSystemDetailDto(this.id, this.name, this.description, this.isPublic, isTemplate, this.formula);
    }

    public GradingSystemDetailDto withFormula(@Size(max = 65535, message = "formula to long") @NotNull(message = "formula must be given") String formula) {
        return this.formula == formula ? this : new GradingSystemDetailDto(this.id, this.name, this.description, this.isPublic, this.isTemplate, formula);
    }
}
