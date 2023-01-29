package at.ac.tuwien.sepm.groupphase.backend.constraint.operator.localdate;

import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.ConstraintOperator;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConstraintException;

import java.time.LocalDate;

public class LocalDateNotEqualsConstraint extends LocalDateConstraintOperator {

    public LocalDateNotEqualsConstraint(LocalDate compareValue, String key, String violationMessage) {
        super(compareValue, key, violationMessage);
    }

    @Override
    public void validate(LocalDate value) throws ConstraintException {
        if (value.isEqual(compareValue)) {
            throw new ConstraintException(violationMessage);
        }
    }
}
