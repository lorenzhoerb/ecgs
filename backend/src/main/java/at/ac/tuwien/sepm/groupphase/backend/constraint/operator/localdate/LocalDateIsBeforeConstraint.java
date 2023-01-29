package at.ac.tuwien.sepm.groupphase.backend.constraint.operator.localdate;

import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.ConstraintOperator;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConstraintException;

import java.time.LocalDate;

public class LocalDateIsBeforeConstraint extends LocalDateConstraintOperator {


    public LocalDateIsBeforeConstraint(LocalDate compareValue, String key, String violationMessage) {
        super(compareValue, key, violationMessage);
    }

    @Override
    public void validate(LocalDate value) throws ConstraintException {
        if (!value.isBefore(compareValue)) {
            throw new ConstraintException(violationMessage);
        }
    }
}
