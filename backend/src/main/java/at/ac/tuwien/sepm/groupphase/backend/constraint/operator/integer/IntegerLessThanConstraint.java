package at.ac.tuwien.sepm.groupphase.backend.constraint.operator.integer;

import at.ac.tuwien.sepm.groupphase.backend.exception.ConstraintException;

public class IntegerLessThanConstraint extends IntegerConstraintOperator {

    public IntegerLessThanConstraint(Integer compareValue, String key, String violationMessage) {
        super(compareValue, key, violationMessage);
    }

    @Override
    public void validate(Integer value) throws ConstraintException {
        if (!(value < compareValue)) {
            throw new ConstraintException(violationMessage);
        }
    }
}
