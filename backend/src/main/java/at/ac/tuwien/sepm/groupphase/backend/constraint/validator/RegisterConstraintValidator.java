package at.ac.tuwien.sepm.groupphase.backend.constraint.validator;

import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.ConstraintOperator;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.enumoperator.EnumConstraintOperator;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.integer.IntegerConstraintOperator;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.localdate.LocalDateConstraintOperator;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConstraintException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class RegisterConstraintValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void validate(List<ConstraintOperator<?>> constraints, ApplicationUser user) {
        LOGGER.debug("validate({}, {})", constraints, user);
        List<String> validationErrors = new ArrayList<>();

        var dateOfBirthConstraints = getDateOfBirthConstraints(constraints);
        var ageConstraints = getAgeConstraints(constraints);
        var genderConstraints = getGenderConstraints(constraints);

        Instant instant = user.getDateOfBirth().toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        LocalDate dateOfBirth = zdt.toLocalDate();
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();


        try {
            for (LocalDateConstraintOperator c : dateOfBirthConstraints) {
                c.validate(dateOfBirth);
            }
            for (IntegerConstraintOperator c : ageConstraints) {
                c.validate(age);
            }
            for (EnumConstraintOperator<ApplicationUser.Gender> c : genderConstraints) {
                c.validate(user.getGender());
            }
        } catch (ConstraintException e) {
            validationErrors.add(e.getMessage());
        }


        if (!validationErrors.isEmpty()) {
            throw new ValidationListException("Validation errors when registering for group", validationErrors);
        }
    }

    private List<IntegerConstraintOperator> getAgeConstraints(List<ConstraintOperator<?>> constraints) {
        LOGGER.debug("getAgeConstraint({})", constraints);
        return constraints.stream()
            .filter(c -> c.getKey().equals("age") && c instanceof IntegerConstraintOperator)
            .map(f -> (IntegerConstraintOperator) f)
            .toList();
    }

    private List<LocalDateConstraintOperator> getDateOfBirthConstraints(List<ConstraintOperator<?>> constraints) {
        LOGGER.debug("getDateOfBirthConstraint({})", constraints);
        return constraints.stream()
            .filter(c -> c.getKey().equals("dateOfBirth") && c instanceof LocalDateConstraintOperator)
            .map(f -> (LocalDateConstraintOperator) f)
            .toList();
    }

    private List<EnumConstraintOperator<ApplicationUser.Gender>> getGenderConstraints(List<ConstraintOperator<?>> constraints) {
        LOGGER.debug("getGenderConstraints({})", constraints);
        return constraints.stream()
            .filter(c -> c.getKey().equals("gender") && EnumConstraintOperator.class.isAssignableFrom(c.getClass()))
            .map(f -> (EnumConstraintOperator<ApplicationUser.Gender>) f)
            .toList();
    }
}
