package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.apache.catalina.core.AprLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Date;

@Component
public class Validator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public Validator() {
    }

    public void validateRegistration(ApplicationUser toValidate) {
        LOGGER.trace("validateRegistration {}", toValidate);
        validateName(toValidate.getFirstName());
        validateName(toValidate.getLastName());
        //validateEmail(toValidate.getUser().getEmail());
        //validatePassword(toValidate.getUser().getPassword());
        validateGender(toValidate.getGender());
        validateDateOfBirth(toValidate.getDateOfBirth());
        validateRole(toValidate.getType());
    }

    public void validatePasswordUpdate(String toValidate) {
        LOGGER.trace("validatePasswordUpdate {}", toValidate);
        validatePassword(toValidate);
    }

    private void validateRole(ApplicationUser.Role type) {
        LOGGER.trace("validateRole {}", type);
        if (type == null) {
            throw new ValidationException("Role must not be null!");
        }
        if (!(type == ApplicationUser.Role.PARTICIPANT || type == ApplicationUser.Role.CLUB_MANAGER || type == ApplicationUser.Role.TOURNAMENT_MANAGER)) {
            throw new ValidationException("Role can only be Participant,ClubManager or TournamentManager.");
        }
    }

    private void validateDateOfBirth(Date dateOfBirth) {
        LOGGER.trace("validateDateOfBirth {}", dateOfBirth);
        if (dateOfBirth == null) {
            throw new ValidationException("DateOfBirth must not be null!");
        }
        if (dateOfBirth.after(new Date())) {
            throw new ValidationException("DateOfBirth cant be in the future!");
        }
    }

    private void validateGender(ApplicationUser.Gender gender) {
        LOGGER.trace("validateGender {}", gender);
        if (gender == null) {
            throw new ValidationException("Gender must not be null!");
        }
        if (!(gender == ApplicationUser.Gender.FEMALE || gender == ApplicationUser.Gender.MALE || gender == ApplicationUser.Gender.OTHER)) {
            throw new ValidationException("Gender can only be FEMALE,MALE or OTHER.");
        }
    }

    private void validatePassword(String password) {
        LOGGER.trace("validatePassword {}", password);
        String regEx = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (password == null) {
            throw new ValidationException("Password must not be null!");
        }
        if (password.length() < 8) {
            throw new ValidationException("Password needs at least 8 characters.");
        }
        /*
        if (!password.matches(regEx)) {
            throw new ValidationException("Password needs at least 8 characters with at least one Uppercase,Lowercase,Number and Special Character(@$!%*?&).");
        }
         */
    }

    private void validateEmail(String email) {
        LOGGER.trace("validateEmail {}", email);
        String regEx = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        if (email == null) {
            throw new ValidationException("Email must not be null!");
        }
        if (!email.matches(regEx)) {
            throw new ValidationException("Email is malformed , expected format example: text@domain.tld");
        }
    }

    private void validateName(String name) {
        LOGGER.trace("validateName {}", name);
        String regEx = "[A-Za-zÄäÖöÜü]+";
        if (name == null) {
            throw new ValidationException("Name must not be null!");
        }
        if (!name.matches(regEx)) {
            throw new ValidationException("Name must only consist of letters.");
        }
        if (name.length() < 2) {
            throw new ValidationException("Name must at least have 2 letters.");
        }
    }
}
