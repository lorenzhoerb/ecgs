package at.ac.tuwien.sepm.groupphase.backend.validation.annotation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

public class HasOnlyUniquePropertyValidator implements ConstraintValidator<HasOnlyUniqueProperty, Object[]> {

    private String property;

    @Override
    public void initialize(HasOnlyUniqueProperty constraintAnnotation) {
        property = constraintAnnotation.property();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object[] objects, ConstraintValidatorContext constraintValidatorContext) {
        if (objects == null || objects.length == 0) {
            return true;
        }

        int uniqueElements = Arrays.stream(objects)
            .map(o -> new BeanWrapperImpl(o).getPropertyValue(property))
            .collect(Collectors.toSet())
            .size();

        return uniqueElements == objects.length;
    }
}
