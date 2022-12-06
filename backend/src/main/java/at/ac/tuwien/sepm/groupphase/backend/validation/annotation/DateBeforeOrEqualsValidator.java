package at.ac.tuwien.sepm.groupphase.backend.validation.annotation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.chrono.ChronoLocalDateTime;

public class DateBeforeOrEqualsValidator implements ConstraintValidator<DateBeforeOrEquals, Object> {

    private String first;
    private String second;

    @Override
    public void initialize(DateBeforeOrEquals constraintAnnotation) {
        first = constraintAnnotation.first();
        second = constraintAnnotation.second();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        Object firstValue = new BeanWrapperImpl(o)
            .getPropertyValue(first);
        Object secondValue = new BeanWrapperImpl(o)
            .getPropertyValue(second);

        if (firstValue instanceof ChronoLocalDateTime && secondValue instanceof ChronoLocalDateTime) {
            return ((ChronoLocalDateTime<?>) firstValue).isBefore((ChronoLocalDateTime<?>) secondValue)
                || ((ChronoLocalDateTime<?>) firstValue).isEqual((ChronoLocalDateTime<?>) secondValue);
        }
        return false;
    }
}
