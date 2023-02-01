package at.ac.tuwien.sepm.groupphase.backend.constraint.operator.integer;

import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.ConstraintOperator;

/**
 * ConstraintOperator specifically for integer types.
 */
public abstract class IntegerConstraintOperator extends ConstraintOperator<Integer> {
    public IntegerConstraintOperator(Integer compareValue, String key, String violationMessage) {
        super(compareValue, key, violationMessage);
    }
}
