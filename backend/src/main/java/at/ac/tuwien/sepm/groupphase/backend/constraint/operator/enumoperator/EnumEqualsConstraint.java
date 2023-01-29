package at.ac.tuwien.sepm.groupphase.backend.constraint.operator.enumoperator;

import at.ac.tuwien.sepm.groupphase.backend.exception.ConstraintException;


public class EnumEqualsConstraint<T extends Enum<T>> extends EnumConstraintOperator<T> {

    public EnumEqualsConstraint(T compareValue, String key, String violationMessage) {
        super(compareValue, key, violationMessage);
    }

    @Override
    public void validate(T value) throws ConstraintException {
        if (!compareValue.equals(value)) {
            throw new ConstraintException(violationMessage);
        }
    }
}
