package at.ac.tuwien.sepm.groupphase.backend.constraint.operator.localdate;

import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.ConstraintOperator;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConstraintException;

import java.time.LocalDate;

public class LocalDateIsAfterConstraint extends LocalDateConstraintOperator {

    public LocalDateIsAfterConstraint(LocalDate compareValue, String key, String violationMessage) {
        super(compareValue, key, violationMessage);
    }

    @Override
    public void validate(LocalDate value) throws ConstraintException {
        if (!value.isAfter(compareValue)) {
            throw new ConstraintException(violationMessage);
        }
    }
}
