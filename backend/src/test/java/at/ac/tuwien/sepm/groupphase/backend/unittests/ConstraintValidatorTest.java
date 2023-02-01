package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.ConstraintOperator;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.enumoperator.EnumEqualsConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.integer.IntegerGreaterThanConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.integer.IntegerLessOrEqualsThanConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.localdate.LocalDateEqualsConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.localdate.LocalDateIsAfterConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.localdate.LocalDateIsBeforeConstraint;
import at.ac.tuwien.sepm.groupphase.backend.constraint.validator.RegisterConstraintValidator;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ConstraintValidatorTest {

    @Autowired
    RegisterConstraintValidator registerConstraintValidator;

    @Test
    public void testRegisterValidator_withInvalidDate_expectValidationError() {
        var g1 = new LocalDateEqualsConstraint(LocalDate.now().minusDays(1), "dateOfBirth", "date of birth must be equals");
        var g2 = new IntegerLessOrEqualsThanConstraint(10, "age", "Age must be below or equals to 10");
        var g3 = new EnumEqualsConstraint<>(ApplicationUser.Gender.MALE, "gender", "Gender must be type: " + ApplicationUser.Gender.MALE);
        List<ConstraintOperator<?>> constraints = List.of(g1, g2, g3);

        ApplicationUser user = new ApplicationUser();
        user.setDateOfBirth(new Date());

        var exception = assertThrows(ValidationListException.class, () -> {
            registerConstraintValidator.validate(constraints, user);
        });

        assertEquals(1, exception.errors().size());
        assertTrue(exception.errors().get(0).contains("date of birth must be equals"));
    }

    @Test
    public void testRegisterValidator_withValidValues_expectNoError() {
        var g1 = new LocalDateEqualsConstraint(LocalDate.now(), "dateOfBirth", "date of birth must be equals");
        var g3 = new EnumEqualsConstraint<>(ApplicationUser.Gender.MALE, "gender", "Gender must be type: " + ApplicationUser.Gender.MALE);
        List<ConstraintOperator<?>> constraints = List.of(g1, g3);

        ApplicationUser user = new ApplicationUser();
        user.setDateOfBirth(new Date());
        user.setGender(ApplicationUser.Gender.MALE);
        assertDoesNotThrow(() -> registerConstraintValidator.validate(constraints, user));
    }

    @Test
    public void testRegisterValidator_withInvalidGender_expectValidationError() {
        var g3 = new EnumEqualsConstraint<>(ApplicationUser.Gender.MALE, "gender", "Gender must be type: " + ApplicationUser.Gender.MALE);
        List<ConstraintOperator<?>> constraints = List.of(g3);

        ApplicationUser user = new ApplicationUser();
        user.setDateOfBirth(new Date());
        user.setGender(ApplicationUser.Gender.FEMALE);
        var exception = assertThrows(ValidationListException.class, () -> {
            registerConstraintValidator.validate(constraints, user);
        });
        assertEquals(exception.errors().size(), 1);
        assertTrue(exception.errors().get(0).contains("Gender must be type"));
    }

    @Test
    public void testRegisterValidator_withValidGender_expectNoError() {
        var g3 = new EnumEqualsConstraint<>(ApplicationUser.Gender.MALE, "gender", "Gender must be type: " + ApplicationUser.Gender.MALE);
        List<ConstraintOperator<?>> constraints = List.of(g3);

        ApplicationUser user = new ApplicationUser();
        user.setDateOfBirth(new Date());
        user.setGender(ApplicationUser.Gender.MALE);
        assertDoesNotThrow(() -> {
            registerConstraintValidator.validate(constraints, user);
        });
    }

    @Test
    public void testRegisterValidator_greaterThenConstraint_withValidAge_expectNoError() {
        var g3 = new IntegerGreaterThanConstraint(5, "age", "age");
        List<ConstraintOperator<?>> constraints = List.of(g3);

        ApplicationUser user = new ApplicationUser();
        user.setDateOfBirth(new Date(90, 11, 23));
        user.setGender(ApplicationUser.Gender.MALE);
        assertDoesNotThrow(() -> {
            registerConstraintValidator.validate(constraints, user);
        });
    }

    @Test
    public void testRegisterValidator_greaterThenConstraint_withInvalidValidAge_expectValidationError() {
        var g3 = new IntegerGreaterThanConstraint(50, "age", "You must be older");
        List<ConstraintOperator<?>> constraints = List.of(g3);

        ApplicationUser user = new ApplicationUser();
        user.setDateOfBirth(new Date(90, 11, 23));
        user.setGender(ApplicationUser.Gender.MALE);

        var exception = assertThrows(ValidationListException.class, () -> {
            registerConstraintValidator.validate(constraints, user);
        });
        assertEquals(exception.errors().size(), 1);
        assertTrue(exception.errors().get(0).contains("You must be older"));
    }

    @Test
    public void testRegisterValidator_BornBefore_withInvalidDateOfBirth_expectValidationError() {
        var g3 = new LocalDateIsBeforeConstraint(LocalDate.of(1999,1,1), "dateOfBirth", "");
        List<ConstraintOperator<?>> constraints = List.of(g3);

        ApplicationUser user = new ApplicationUser();
        user.setDateOfBirth(new Date(99, 1, 1));
        user.setGender(ApplicationUser.Gender.MALE);

        var exception = assertThrows(ValidationListException.class, () -> {
            registerConstraintValidator.validate(constraints, user);
        });
        assertEquals(exception.errors().size(), 1);
    }

    @Test
    public void testRegisterValidator_BornBefore_withValidDateOfBirth_expectNoError() {
        var g3 = new LocalDateIsBeforeConstraint(LocalDate.of(1999,1,2), "dateOfBirth", "");
        List<ConstraintOperator<?>> constraints = List.of(g3);

        ApplicationUser user = new ApplicationUser();
        user.setDateOfBirth(new Date(98, 1, 1));
        user.setGender(ApplicationUser.Gender.MALE);

        assertDoesNotThrow(() -> registerConstraintValidator.validate(constraints, user));
    }

    @Test
    public void testRegisterValidator_BornAfter_withInvalidDateOfBirth_expectValidationError() {
        var g3 = new LocalDateIsAfterConstraint(LocalDate.of(1999,1,1), "dateOfBirth", "");
        List<ConstraintOperator<?>> constraints = List.of(g3);

        ApplicationUser user = new ApplicationUser();
        user.setDateOfBirth(new Date(98, 1, 1));
        user.setGender(ApplicationUser.Gender.MALE);

        var exception = assertThrows(ValidationListException.class, () -> {
            registerConstraintValidator.validate(constraints, user);
        });
        assertEquals(exception.errors().size(), 1);
    }

    @Test
    public void testRegisterValidator_BornAfter_withValidDateOfBirth_expectNoError() {
        var g3 = new LocalDateIsAfterConstraint(LocalDate.of(1999,1,2), "dateOfBirth", "");
        List<ConstraintOperator<?>> constraints = List.of(g3);

        ApplicationUser user = new ApplicationUser();
        user.setDateOfBirth(new Date(99, 1, 3));
        user.setGender(ApplicationUser.Gender.MALE);

        assertDoesNotThrow(() -> registerConstraintValidator.validate(constraints, user));
    }
}
