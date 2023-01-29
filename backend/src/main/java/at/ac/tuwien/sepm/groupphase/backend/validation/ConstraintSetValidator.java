package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BasicRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ConstraintSetValidator extends Validator<List<BasicRegisterConstraintDto>> {

    private final ConstraintValidator constraintValidator;

    protected ConstraintSetValidator(javax.validation.Validator validator, ConstraintValidator constraintValidator) {
        super(validator);
        this.constraintValidator = constraintValidator;
    }

    @Override
    protected void validateCustom(List<BasicRegisterConstraintDto> toValidate) {
        List<String> validationErrors = new ArrayList<>();
        for (BasicRegisterConstraintDto constraint : toValidate) {
            try {
                constraintValidator.validate(constraint);
            } catch (ValidationListException e) {
                validationErrors.addAll(e.errors());
            }
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationListException("Error validating constraints", validationErrors);
        }
    }
}
