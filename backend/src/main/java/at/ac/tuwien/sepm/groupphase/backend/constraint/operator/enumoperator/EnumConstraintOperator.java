package at.ac.tuwien.sepm.groupphase.backend.constraint.operator.enumoperator;

import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.ConstraintOperator;

public abstract class EnumConstraintOperator<T extends Enum<T>> extends ConstraintOperator<T> {
    public EnumConstraintOperator(T compareValue, String key, String violationMessage) {
        super(compareValue, key, violationMessage);
    }
}
