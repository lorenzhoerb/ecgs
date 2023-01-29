package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class GradingGroupDto {
    private Long id;

    @NotBlank(message = "Grading Groups must have a name")
    @NotNull(message = "Grading Groups must have a name")
    @Size(max = 4095)
    private String title;

    private GradingSystemDetailDto gradingSystemDto;

    public GradingGroupDto() {
    }

    public GradingGroupDto(String title, GradingSystemDetailDto gradingSystemDto) {
        this.title = title;
        this.gradingSystemDto = gradingSystemDto;
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public GradingSystemDetailDto getGradingSystemDto() {
        return this.gradingSystemDto;
    }

    public GradingGroupDto withId(Long id) {
        this.id = id;
        return this;
    }

    public GradingGroupDto withTitle(String title) {
        this.title = title;
        return this;
    }

    public GradingGroupDto withGradingSystemDto(GradingSystemDetailDto gradingSystemDto) {
        this.gradingSystemDto = gradingSystemDto;
        return this;
    }

    public String toString() {
        return "GradingGroupDto(id=" + this.getId() + ", title=" + this.getTitle() + ", gradingSystemDto=" + this.getGradingSystemDto() + ")";
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGradingSystemDto(GradingSystemDetailDto gradingSystemDto) {
        this.gradingSystemDto = gradingSystemDto;
    }
}
