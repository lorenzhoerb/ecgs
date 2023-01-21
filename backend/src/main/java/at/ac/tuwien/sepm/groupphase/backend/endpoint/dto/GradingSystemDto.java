package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    private Boolean isTemplate;

    @NotNull(message = "Client Error")
    private String formula;

    public GradingSystemDto() {
    }

    public GradingSystemDto(Long id, String name, String description, Boolean isPublic, Boolean isTemplate, String formula) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.isTemplate = isTemplate;
        this.formula = formula;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean isPublic) {
        isPublic = isPublic;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    @Override
    public String toString() {
        return "GradingSystemDto{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", description='" + description + '\''
            + ", isPublic=" + isPublic
            + ", formula='" + formula + '\''
            + '}';
    }
}
