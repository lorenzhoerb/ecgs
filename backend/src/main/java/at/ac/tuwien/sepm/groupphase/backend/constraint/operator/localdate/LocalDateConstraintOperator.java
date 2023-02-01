package at.ac.tuwien.sepm.groupphase.backend.constraint.operator.localdate;

import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.ConstraintOperator;

import java.time.LocalDate;

/**
 * ConstraintOperator specifically for the LocalDate type.
 */
public abstract class LocalDateConstraintOperator extends ConstraintOperator<LocalDate> {
    public LocalDateConstraintOperator(LocalDate compareValue, String key, String violationMessage) {
        super(compareValue, key, violationMessage);
    }
}
