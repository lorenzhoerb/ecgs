package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;

public class DetailedGradingGroupDto {
    private Long id;
    private String title;
    private List<DetailedRegisterConstraintDto> constraints;

    public DetailedGradingGroupDto(Long id, String title, List<DetailedRegisterConstraintDto> constraints) {
        this.id = id;
        this.title = title;
        this.constraints = constraints;
    }

    public DetailedGradingGroupDto() {
    }

    public static DetailedGradingGroupDtoBuilder builder() {
        return new DetailedGradingGroupDtoBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public List<DetailedRegisterConstraintDto> getConstraints() {
        return this.constraints;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setConstraints(List<DetailedRegisterConstraintDto> constraints) {
        this.constraints = constraints;
    }

    public static class DetailedGradingGroupDtoBuilder {
        private Long id;
        private String title;
        private List<DetailedRegisterConstraintDto> constraints;

        DetailedGradingGroupDtoBuilder() {
        }

        public DetailedGradingGroupDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DetailedGradingGroupDtoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public DetailedGradingGroupDtoBuilder constraints(List<DetailedRegisterConstraintDto> constraints) {
            this.constraints = constraints;
            return this;
        }

        public DetailedGradingGroupDto build() {
            return new DetailedGradingGroupDto(id, title, constraints);
        }

        public String toString() {
            return "DetailedGradingGroupDto.DetailedGradingGroupDtoBuilder(id=" + this.id + ", title=" + this.title + ", constraints=" + this.constraints + ")";
        }
    }
}
