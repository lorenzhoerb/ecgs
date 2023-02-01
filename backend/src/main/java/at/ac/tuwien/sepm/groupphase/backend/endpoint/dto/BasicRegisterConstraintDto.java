package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BasicRegisterConstraintDto {

    @NotNull(message = "Type must be given")
    private RegisterConstraint.ConstraintType type;

    @NotNull(message = "Operator must be given")
    private RegisterConstraint.Operator operator;

    @NotEmpty(message = "Value must be given")
    @Size(min = 1, max = 256, message = "Length of value must be between 1 and 256")
    private String value;

    public BasicRegisterConstraintDto(@NotNull(message = "Type must be given") RegisterConstraint.ConstraintType type,
                                      @NotNull(message = "Operator must be given") RegisterConstraint.Operator operator,
                                      @NotEmpty(message = "Value must be given") @Size(min = 1, max = 256, message = "Length of value must be between 1 and 256") String value) {
        this.type = type;
        this.operator = operator;
        this.value = value;
    }

    public BasicRegisterConstraintDto() {
    }

    public static BasicRegisterConstraintDtoBuilder builder() {
        return new BasicRegisterConstraintDtoBuilder();
    }

    public RegisterConstraint.@NotNull(message = "Type must be given") ConstraintType getType() {
        return this.type;
    }

    public RegisterConstraint.@NotNull(message = "Operator must be given") Operator getOperator() {
        return this.operator;
    }

    public @NotEmpty(message = "Value must be given") @Size(min = 1, max = 256, message = "Length of value must be between 1 and 256") String getValue() {
        return this.value;
    }

    public void setType(@NotNull(message = "Type must be given") RegisterConstraint.ConstraintType type) {
        this.type = type;
    }

    public void setOperator(@NotNull(message = "Operator must be given") RegisterConstraint.Operator operator) {
        this.operator = operator;
    }

    public void setValue(
        @NotEmpty(message = "Value must be given") @Size(min = 1, max = 256, message = "Length of value must be between 1 and 256") String value) {
        this.value = value;
    }

    public static class BasicRegisterConstraintDtoBuilder {
        private RegisterConstraint.@NotNull(message = "Type must be given") ConstraintType type;
        private RegisterConstraint.@NotNull(message = "Operator must be given") Operator operator;
        private @NotEmpty(message = "Value must be given") @Size(min = 1, max = 256, message = "Length of value must be between 1 and 256") String value;

        BasicRegisterConstraintDtoBuilder() {
        }

        public BasicRegisterConstraintDtoBuilder type(@NotNull(message = "Type must be given") RegisterConstraint.ConstraintType type) {
            this.type = type;
            return this;
        }

        public BasicRegisterConstraintDtoBuilder operator(
            @NotNull(message = "Operator must be given") RegisterConstraint.Operator operator) {
            this.operator = operator;
            return this;
        }

        public BasicRegisterConstraintDtoBuilder value(
            @NotEmpty(message = "Value must be given") @Size(min = 1, max = 256, message = "Length of value must be between 1 and 256") String value) {
            this.value = value;
            return this;
        }

        public BasicRegisterConstraintDto build() {
            return new BasicRegisterConstraintDto(type, operator, value);
        }

        public String toString() {
            return "BasicRegisterConstraintDto.BasicRegisterConstraintDtoBuilder(type=" + this.type + ", operator=" + this.operator + ", value=" + this.value
                + ")";
        }
    }
}
