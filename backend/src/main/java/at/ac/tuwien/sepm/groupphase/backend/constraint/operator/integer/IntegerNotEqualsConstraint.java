package at.ac.tuwien.sepm.groupphase.backend.constraint.operator.integer;

import at.ac.tuwien.sepm.groupphase.backend.exception.ConstraintException;

import java.util.Objects;

public class IntegerNotEqualsConstraint extends IntegerConstraintOperator {

    public IntegerNotEqualsConstraint(Integer compareValue, String key, String violationMessage) {
        super(compareValue, key, violationMessage);
    }

    @Override
    public void validate(Integer value) throws ConstraintException {
        if (Objects.equals(compareValue, value)) {
            throw new ConstraintException(violationMessage);
        }
    }
}
