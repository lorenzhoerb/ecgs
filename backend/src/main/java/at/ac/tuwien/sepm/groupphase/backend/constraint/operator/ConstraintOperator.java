package at.ac.tuwien.sepm.groupphase.backend.constraint.operator;

import at.ac.tuwien.sepm.groupphase.backend.exception.ConstraintException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public abstract class ConstraintOperator<T> {

    protected final T compareValue;
    protected final String violationMessage;
    protected final String key;

    public ConstraintOperator(T compareValue, String key, String violationMessage) {
        this.compareValue = compareValue;
        this.violationMessage = violationMessage;
        this.key = key;
    }

    public abstract void validate(T value) throws ConstraintException;

    public String getKey() {
        return key;
    }

    public String getViolationMessage() {
        return violationMessage;
    }
}