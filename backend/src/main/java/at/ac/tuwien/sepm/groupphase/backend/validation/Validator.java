package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Set;

/**
 * This abstract class is used for specific validators. It has a default validate methode where the
 * java javax.validation.constraints annotations get validated. If needed a custom validation can be implemented
 * in validateCustom.
 *
 * @param <T> Class to be validated
 */
@Component
public abstract class Validator<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final javax.validation.Validator validator;

    protected Validator(javax.validation.Validator validator) {
        this.validator = validator;
    }

    /**
     * Validates the object T. It first validates javax.validation.constraints annotations and then if implemented
     * the custom validation.
     *
     * @param toValidate Object to validate
     * @throws ValidationListException A validation runtime exception if the annotation fails to validate or if the custom validation throws it.
     */
    public void validate(T toValidate) {
        LOGGER.trace("Validate {}", toValidate);
        validateAnnotations(toValidate);
        validateCustom(toValidate);
    }

    /**
     * Validates the javax.validation.constraints for the given object toValidate.
     *
     * @param toValidate Object to be validate
     */
    protected void validateAnnotations(T toValidate) {
        LOGGER.trace("Validate annotations of {}", toValidate);
        Set<ConstraintViolation<T>> violations = validator.validate(toValidate);
        if (!violations.isEmpty()) {
            List<String> errMessages = violations.stream().map(ConstraintViolation::getMessage).toList();
            throw new ValidationListException("Validation failed", errMessages);
        }
    }

    /**
     * Custom validation code. Optional to implement.
     * Following RuntimeErrors can be thrown: ValidationListException, ConflictException
     *
     * @param toValidate Object to validate
     */
    protected abstract void validateCustom(T toValidate);
}
