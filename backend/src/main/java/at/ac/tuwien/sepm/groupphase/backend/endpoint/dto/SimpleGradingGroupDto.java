package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;

public class SimpleGradingGroupDto {
    Long id;
    String title;
    List<BasicDto> constraints;

    public SimpleGradingGroupDto(Long id, String title, List<BasicDto> constraints) {
        this.id = id;
        this.title = title;
        this.constraints = constraints;
    }

    public SimpleGradingGroupDto() {
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public List<BasicDto> getConstraints() {
        return this.constraints;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setConstraints(List<BasicDto> constraints) {
        this.constraints = constraints;
    }
}
