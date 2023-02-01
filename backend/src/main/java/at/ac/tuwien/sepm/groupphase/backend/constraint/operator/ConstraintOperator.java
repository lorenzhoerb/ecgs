package at.ac.tuwien.sepm.groupphase.backend.constraint.operator;

import at.ac.tuwien.sepm.groupphase.backend.exception.ConstraintException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * ConstraintOperator encapsulating a constraint for a grading group.
 */
public abstract class ConstraintOperator<T> {

    protected final T compareValue;
    protected final String violationMessage;
    protected final String key;

    /**
     * Constructor.
     *
     * @param compareValue     value used to apply constraint to
     * @param key              identifier for the constraint
     * @param violationMessage Message used for error handling in the event of constraint violation
     */
    public ConstraintOperator(T compareValue, String key, String violationMessage) {
        this.compareValue = compareValue;
        this.violationMessage = violationMessage;
        this.key = key;
    }

    /**
     * validates the given value by its specified type implemented in the child classes
     * IntegerConstraint, EnumConstraint and LocalDateConstraint.
     *
     * @param value the value to validate under the constraint
     * @throws ConstraintException when value is not valid under constraint
     */
    public abstract void validate(T value) throws ConstraintException;

    /**
     * Gets the key for the constraint.
     *
     * @return the current key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the message to pass to an exception in the violation case.
     *
     * @return the set violation message
     */
    public String getViolationMessage() {
        return violationMessage;
    }
}