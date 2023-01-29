package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BasicRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ConstraintValidator extends Validator<BasicRegisterConstraintDto> {

    private static List<RegisterConstraint.Operator> ageOperators = List.of(
        RegisterConstraint.Operator.EQUALS,
        RegisterConstraint.Operator.NOT_EQUALS,
        RegisterConstraint.Operator.GREATER_THAN,
        RegisterConstraint.Operator.GREATER_EQUALS_THAN,
        RegisterConstraint.Operator.LESS_THAN,
        RegisterConstraint.Operator.LESS_EQUALS_THAN
    );

    private static List<RegisterConstraint.Operator> dateOfBirth = List.of(
        RegisterConstraint.Operator.EQUALS,
        RegisterConstraint.Operator.NOT_EQUALS,
        RegisterConstraint.Operator.BORN_BEFORE,
        RegisterConstraint.Operator.BORN_AFTER
    );

    private static List<RegisterConstraint.Operator> gender = List.of(
        RegisterConstraint.Operator.EQUALS,
        RegisterConstraint.Operator.NOT_EQUALS
    );

    protected ConstraintValidator(javax.validation.Validator validator) {
        super(validator);
    }

    @Override
    protected void validateCustom(BasicRegisterConstraintDto toValidate) {
        List<String> validationErrors = new ArrayList<>();
        switch (toValidate.getType()) {
            case AGE:
                try {
                    int age = Integer.parseInt(toValidate.getValue());
                    if (age < 1) {
                        validationErrors.add("Invalid age: " + age + ".Age must be greater than or equals 1");
                    }
                } catch (NumberFormatException e) {
                    validationErrors.add("Invalid age format: " + toValidate.getValue() + ". Age must be type integer");
                }
                if (!ageOperators.contains(toValidate.getOperator())) {
                    validationErrors.add("Invalid operator for age: " + toValidate.getOperator().toString());
                }
                break;
            case DATE_OF_BIRTH:
                try {
                    LocalDate.parse(toValidate.getValue());
                } catch (DateTimeParseException e) {
                    validationErrors.add("Invalid dateOfBirthFormat: " + toValidate.getValue() + ". Date of birth format must be LocalDate");
                }
                if (!dateOfBirth.contains(toValidate.getOperator())) {
                    validationErrors.add("Invalid operator for date of birth: " + toValidate.getOperator().toString());
                }
                break;
            case GENDER:

                try {
                    ApplicationUser.Gender.valueOf(toValidate.getValue());
                } catch (IllegalArgumentException e) {
                    validationErrors.add("Invalid value for gender: " + toValidate.getValue());
                }
                if (!gender.contains(toValidate.getOperator())) {
                    validationErrors.add("Invalid operator for gender: " + toValidate.getOperator().toString());
                }
                break;
            default:
                validationErrors.add("Unsupported constraint type");
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationListException("Error validating constraint", validationErrors);
        }
    }
}
