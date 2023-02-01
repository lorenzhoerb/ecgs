package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class BasicDto {
    private String description;

    public BasicDto(String description) {
        this.description = description;
    }

    public BasicDto() {
    }

    public static BasicDtoBuilder builder() {
        return new BasicDtoBuilder();
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class BasicDtoBuilder {
        private String description;

        BasicDtoBuilder() {
        }

        public BasicDtoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public BasicDto build() {
            return new BasicDto(description);
        }

        public String toString() {
            return "BasicDto.BasicDtoBuilder(description=" + this.description + ")";
        }
    }
}
