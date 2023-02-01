package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.report.ranking.parser.RegisterConstraintParser;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConstraintParserException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RegisterConstraintParserTest {

    @Autowired
    private RegisterConstraintParser registerConstraintParser;

    @Test
    public void parse_forAge_withValidRegistrationDto() {
        var constraint = RegisterConstraint.builder()
            .type(RegisterConstraint.ConstraintType.AGE)
            .operator(RegisterConstraint.Operator.EQUALS)
            .constraintValue("1")
            .build();
        assertDoesNotThrow(() -> registerConstraintParser.parse(constraint));
    }

    @Test
    public void parse_forAge_withInvalidAgeFormat_withChar_expectConstraintParserException() {
        var constraint = RegisterConstraint.builder()
            .type(RegisterConstraint.ConstraintType.AGE)
            .operator(RegisterConstraint.Operator.EQUALS)
            .constraintValue("a1")
            .build();
        ConstraintParserException exception = assertThrows(ConstraintParserException.class, () -> registerConstraintParser.parse(constraint));
        assertTrue(exception.getMessage().contains("Age must be a number"));
    }

    @Test
    public void parse_forAge_withInvalidAgeFormat_withAgeLessThen0_expectConstraintParserException() {
        var constraint = RegisterConstraint.builder()
            .type(RegisterConstraint.ConstraintType.AGE)
            .operator(RegisterConstraint.Operator.EQUALS)
            .constraintValue("-4")
            .build();
        ConstraintParserException exception = assertThrows(ConstraintParserException.class, () -> registerConstraintParser.parse(constraint));
        assertTrue(exception.getMessage().contains("Age must be greater then 0"));
    }

    @Test
    public void parse_forAge_withInvalidOperator_expectConstraintParserException() {
        var constraint = RegisterConstraint.builder()
            .type(RegisterConstraint.ConstraintType.AGE)
            .operator(RegisterConstraint.Operator.BORN_AFTER)
            .constraintValue("1")
            .build();
        ConstraintParserException exception = assertThrows(ConstraintParserException.class, () -> registerConstraintParser.parse(constraint));
        assertTrue(exception.getMessage().contains("Unsupported operator for age"));
    }

    @Test
    public void parse_forGender_withValidRegistrationDto() {
        var constraint = RegisterConstraint.builder()
            .type(RegisterConstraint.ConstraintType.GENDER)
            .operator(RegisterConstraint.Operator.EQUALS)
            .constraintValue("FEMALE")
            .build();
        assertDoesNotThrow(() -> registerConstraintParser.parse(constraint));
    }

    @Test
    public void parse_forGender_notEquals_withValidRegistrationDto() {
        var constraint = RegisterConstraint.builder()
            .type(RegisterConstraint.ConstraintType.GENDER)
            .operator(RegisterConstraint.Operator.NOT_EQUALS)
            .constraintValue("FEMALE")
            .build();
        assertDoesNotThrow(() -> registerConstraintParser.parse(constraint));
    }

    @Test
    public void parse_forGender_withInvalidGender_expectConstraintParserException() {
        var constraint = RegisterConstraint.builder()
            .type(RegisterConstraint.ConstraintType.GENDER)
            .operator(RegisterConstraint.Operator.EQUALS)
            .constraintValue("HUND")
            .build();
        var exception = assertThrows(ConstraintParserException.class, () -> registerConstraintParser.parse(constraint));
        assertTrue(exception.getMessage().contains("Invalid gender"));
    }

    @Test
    public void parse_forGender_withInvalidOperator_expectConstraintParserException() {
        var constraint = RegisterConstraint.builder()
            .type(RegisterConstraint.ConstraintType.GENDER)
            .operator(RegisterConstraint.Operator.BORN_BEFORE)
            .constraintValue("MALE")
            .build();
        var exception = assertThrows(ConstraintParserException.class, () -> registerConstraintParser.parse(constraint));
        assertTrue(exception.getMessage().contains("Unsupported operator for gender"));
    }

    @Test
    public void parse_forDateOfBirth_withValidRegistrationDto() {
        var constraint = RegisterConstraint.builder()
            .type(RegisterConstraint.ConstraintType.DATE_OF_BIRTH)
            .operator(RegisterConstraint.Operator.BORN_BEFORE)
            .constraintValue("1999-11-23")
            .build();
        assertDoesNotThrow(() -> registerConstraintParser.parse(constraint));
    }

    @Test
    public void parse_forDateOfBirth_withInvalidDateOfBirthFormat_expectConstraintParserException() {
        var constraint = RegisterConstraint.builder()
            .type(RegisterConstraint.ConstraintType.DATE_OF_BIRTH)
            .operator(RegisterConstraint.Operator.EQUALS)
            .constraintValue("1999!11.23")
            .build();
        var exception = assertThrows(ConstraintParserException.class, () -> registerConstraintParser.parse(constraint));
        assertTrue(exception.getMessage().contains("Invalid date of birth format. Expected format is 'yyyy-MM-dd'"));
    }
}
