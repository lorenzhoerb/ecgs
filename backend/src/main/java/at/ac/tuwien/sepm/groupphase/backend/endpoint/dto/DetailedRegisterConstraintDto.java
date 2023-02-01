package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;

public class DetailedRegisterConstraintDto {
    private Long id;
    private RegisterConstraint.ConstraintType type;
    private RegisterConstraint.Operator operator;
    private String value;

    public DetailedRegisterConstraintDto(Long id, RegisterConstraint.ConstraintType type, RegisterConstraint.Operator operator, String value) {
        this.id = id;
        this.type = type;
        this.operator = operator;
        this.value = value;
    }

    public DetailedRegisterConstraintDto() {
    }

    public static DetailedRegisterConstraintDtoBuilder builder() {
        return new DetailedRegisterConstraintDtoBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public RegisterConstraint.ConstraintType getType() {
        return this.type;
    }

    public RegisterConstraint.Operator getOperator() {
        return this.operator;
    }

    public String getValue() {
        return this.value;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(RegisterConstraint.ConstraintType type) {
        this.type = type;
    }

    public void setOperator(RegisterConstraint.Operator operator) {
        this.operator = operator;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static class DetailedRegisterConstraintDtoBuilder {
        private Long id;
        private RegisterConstraint.ConstraintType type;
        private RegisterConstraint.Operator operator;
        private String value;

        DetailedRegisterConstraintDtoBuilder() {
        }

        public DetailedRegisterConstraintDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DetailedRegisterConstraintDtoBuilder type(RegisterConstraint.ConstraintType type) {
            this.type = type;
            return this;
        }

        public DetailedRegisterConstraintDtoBuilder operator(RegisterConstraint.Operator operator) {
            this.operator = operator;
            return this;
        }

        public DetailedRegisterConstraintDtoBuilder value(String value) {
            this.value = value;
            return this;
        }

        public DetailedRegisterConstraintDto build() {
            return new DetailedRegisterConstraintDto(id, type, operator, value);
        }

        public String toString() {
            return "DetailedRegisterConstraintDto.DetailedRegisterConstraintDtoBuilder(id=" + this.id + ", type=" + this.type + ", operator=" + this.operator
                + ", value=" + this.value + ")";
        }
    }
}
