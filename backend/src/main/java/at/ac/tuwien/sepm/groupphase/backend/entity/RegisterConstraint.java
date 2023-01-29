package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Builder;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@ToString
@Builder
public class RegisterConstraint {

    public enum ConstraintType {
        AGE,
        DATE_OF_BIRTH,
        GENDER
    }

    public enum Operator {
        EQUALS,
        NOT_EQUALS,
        GREATER_THAN,
        GREATER_EQUALS_THAN,
        LESS_THAN,
        LESS_EQUALS_THAN,
        BORN_BEFORE,
        BORN_AFTER,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private ConstraintType type;

    @Column(nullable = false)
    private Operator operator;

    @Column(nullable = false)
    private String constraintValue;

    @ManyToOne
    @JoinColumn(name = "grading_group_id")
    private GradingGroup gradingGroup;

    public RegisterConstraint() {
    }

    public RegisterConstraint(Long id, ConstraintType type, Operator operator, String constraintValue, GradingGroup gradingGroup) {
        this.id = id;
        this.type = type;
        this.operator = operator;
        this.constraintValue = constraintValue;
        this.gradingGroup = gradingGroup;
    }

    public Long getId() {
        return id;
    }

    public RegisterConstraint setId(Long id) {
        this.id = id;
        return this;
    }

    public ConstraintType getType() {
        return type;
    }

    public RegisterConstraint setType(ConstraintType type) {
        this.type = type;
        return this;
    }

    public Operator getOperator() {
        return operator;
    }

    public RegisterConstraint setOperator(Operator operator) {
        this.operator = operator;
        return this;
    }

    public String getConstraintValue() {
        return constraintValue;
    }

    public RegisterConstraint setConstraintValue(String value) {
        this.constraintValue = value;
        return this;
    }

    public GradingGroup getGradingGroup() {
        return gradingGroup;
    }

    public RegisterConstraint setGradingGroup(GradingGroup gradingGroup) {
        this.gradingGroup = gradingGroup;
        return this;
    }
}
