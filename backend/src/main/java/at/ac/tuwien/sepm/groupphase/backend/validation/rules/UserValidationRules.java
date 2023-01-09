package at.ac.tuwien.sepm.groupphase.backend.validation.rules;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;

import java.util.Date;
import java.util.List;

public class UserValidationRules {
    public static final String NAME_REGEX = "[A-Za-zÄäÖöÜü]+";
    public static final String EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";


    public static void validateRole(ApplicationUser.Role type, List<String> errors) {
        if (type == null) {
            errors.add("Role was not specified!");
            return;
        }
        if (!(type == ApplicationUser.Role.PARTICIPANT || type == ApplicationUser.Role.CLUB_MANAGER || type == ApplicationUser.Role.TOURNAMENT_MANAGER)) {
            errors.add("Role can only be Participant,ClubManager or CompetitionManager.");
        }
    }

    public static void validateDateOfBirth(Date dateOfBirth, List<String> errors) {
        if (dateOfBirth == null) {
            errors.add("Date of birth was not specified!");
            return;
        }
        if (dateOfBirth.after(new Date())) {
            errors.add("Date of birth cant be in the future!");
        }
    }

    public static void validateGender(ApplicationUser.Gender gender, List<String> errors) {
        if (gender == null) {
            errors.add("Gender was not specified!");
            return;
        }
        if (!(gender == ApplicationUser.Gender.FEMALE || gender == ApplicationUser.Gender.MALE || gender == ApplicationUser.Gender.OTHER)) {
            errors.add("Gender can only be FEMALE,MALE or OTHER.");
        }
    }

    public static void validatePassword(String password, List<String> errors) {
        if (password == null) {
            errors.add("Password was not specified!");
            return;
        }
        if (!password.matches(PASSWORD_REGEX)) {
            errors.add("Password needs at least 8 characters with at least one Uppercase,Lowercase,Number and Special Character(@$!%*?&).");
        }
    }

    public static void validateEmail(String email, List<String> errors) {
        if (email == null) {
            errors.add("Email was not specified!");
            return;
        }
        if (!email.matches(EMAIL_REGEX)) {
            errors.add("Email is malformed , expected format example: text@domain.tld");
        }
    }

    public static void validateFirstName(String name, List<String> errors) {
        if (name == null) {
            errors.add("First name was not specified!");
            return;
        }
        if (!name.matches(NAME_REGEX)) {
            errors.add("First name must only consist of letters.");
        }
        if (name.length() < 2) {
            errors.add("First name must at least have 2 letters.");
        }
    }

    public static void validateLastName(String name, List<String> errors) {
        if (name == null) {
            errors.add("Last name was not specified!");
            return;
        }
        if (!name.matches(NAME_REGEX)) {
            errors.add("Last name must only consist of letters.");
        }
        if (name.length() < 2) {
            errors.add("Last name must at least have 2 letters.");
        }
    }
}
