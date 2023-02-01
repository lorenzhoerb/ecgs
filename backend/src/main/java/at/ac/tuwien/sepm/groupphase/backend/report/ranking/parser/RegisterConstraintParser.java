package at.ac.tuwien.sepm.groupphase.backend.report.ranking.parser;

import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.ConstraintOperator;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.enumoperator.EnumEqualsConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.enumoperator.EnumNotEqualsConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.integer.IntegerEqualsConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.integer.IntegerGreaterOrEqualsThanConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.integer.IntegerGreaterThanConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.integer.IntegerLessOrEqualsThanConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.integer.IntegerLessThanConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.integer.IntegerNotEqualsConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.localdate.LocalDateEqualsConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.localdate.LocalDateIsAfterConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.localdate.LocalDateIsBeforeConstraint;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConstraintParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RegisterConstraintParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public List<ConstraintOperator<?>> parse(List<RegisterConstraint> constraints) throws ConstraintParserException {
        LOGGER.debug("parse({})", constraints);
        List<ConstraintOperator<?>> parsedConstraints = new ArrayList<>();
        for (RegisterConstraint c : constraints) {
            parsedConstraints.add(parse(c));
        }
        return parsedConstraints;
    }

    public ConstraintOperator<?> parse(RegisterConstraint constraint) throws ConstraintParserException {
        LOGGER.debug("parse({})", constraint);
        if (constraint.getType() == null) {
            throw new ConstraintParserException("Type must be given");
        }
        if (constraint.getOperator() == null) {
            throw new ConstraintParserException("Operator must be given");
        }
        if (constraint.getConstraintValue() == null || constraint.getConstraintValue().isEmpty()) {
            throw new ConstraintParserException("Value must be given");
        }

        switch (constraint.getType()) {
            case AGE:
                return parseAge(constraint);
            case DATE_OF_BIRTH:
                return parseDateOfBirth(constraint);
            case GENDER:
                return parseGender(constraint);
            default:
                throw new ConstraintParserException("Unsupported constraint type: " + constraint.getType());
        }
    }

    private ConstraintOperator<Integer> parseAge(RegisterConstraint dto) throws ConstraintParserException {
        LOGGER.debug("parseAge({})", dto);
        int age;
        try {
            age = Integer.parseInt(dto.getConstraintValue());
        } catch (NumberFormatException e) {
            throw new ConstraintParserException("Age must be a number: " + dto.getConstraintValue());
        }

        if (age < 0) {
            throw new ConstraintParserException("Age must be greater then 0: " + dto.getConstraintValue());
        }

        switch (dto.getOperator()) {
            case EQUALS:
                return new IntegerEqualsConstraint(age, "age", "You must be " + age + " years old");
            case NOT_EQUALS:
                return new IntegerNotEqualsConstraint(age, "age", "You must not be " + age + " years old");
            case GREATER_THAN:
                return new IntegerGreaterThanConstraint(age, "age", "You must be older than " + age);
            case GREATER_EQUALS_THAN:
                return new IntegerGreaterOrEqualsThanConstraint(age, "age", "You must be older than " + (age - 1));
            case LESS_THAN:
                return new IntegerLessThanConstraint(age, "age", "You must be younger than " + age);
            case LESS_EQUALS_THAN:
                return new IntegerLessOrEqualsThanConstraint(age, "age", "You must be younger than " + (age + 1));
            default:
                throw new ConstraintParserException("Unsupported operator for age: " + dto.getOperator());
        }
    }

    private ConstraintOperator<LocalDate> parseDateOfBirth(RegisterConstraint dto) throws ConstraintParserException {
        LOGGER.debug("parseDateOfBirth({})", dto);
        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(dto.getConstraintValue());
        } catch (DateTimeParseException e) {
            throw new ConstraintParserException("Invalid date of birth format. Expected format is 'yyyy-MM-dd'");
        }
        switch (dto.getOperator()) {
            case BORN_BEFORE:
                return new LocalDateIsBeforeConstraint(dateOfBirth, "dateOfBirth", "Date of birth must be before " + dateOfBirth.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            case BORN_AFTER:
                return new LocalDateIsAfterConstraint(dateOfBirth, "dateOfBirth", "Date of birth must be after " + dateOfBirth.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            default:
                throw new ConstraintParserException("Unsupported operator for date of birth: " + dto.getOperator());
        }
    }

    private ConstraintOperator<ApplicationUser.Gender> parseGender(RegisterConstraint dto) throws ConstraintParserException {
        LOGGER.debug("parseGender({})", dto);
        ApplicationUser.Gender gender;
        try {
            gender = ApplicationUser.Gender.valueOf(dto.getConstraintValue().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ConstraintParserException("Invalid gender");
        }
        switch (dto.getOperator()) {
            case EQUALS:
                return new EnumEqualsConstraint<>(gender, "gender", "Gender must be " + gender.toString().toLowerCase());
            case NOT_EQUALS:
                return new EnumNotEqualsConstraint<>(gender, "gender", "Gender must not be " + gender.toString().toLowerCase());
            default:
                throw new ConstraintParserException("Unsupported operator for gender: " + dto.getOperator());
        }
    }
}
