package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser.Gender;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

public record ClubManagerTeamMemberImportDto(
    @NotBlank(message = "First name must not be blank") // basically min = 1
    @Size(max = 255, message = "First name must be shorter")
    @Pattern(regexp = "^[a-zA-Z \\u00C0-\\u017F-]*$", message = "First name can only include letters, spaces and -")
    String firstName,

    @NotBlank(message = "Last name must not be blank") // basically min = 1
    @Size(max = 255, message = "Last name must be shorter")
    @Pattern(regexp = "^[a-zA-Z \\u00C0-\\u017F-]*$", message = "Last name can only include letters, spaces and -")
    String lastName,

    @NotNull(message = "Gender field is blank")
    Gender gender,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Date of birth must be given")
    Date dateOfBirth,

    @NotBlank(message = "Email must be given")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,63}",
        flags = Pattern.Flag.CASE_INSENSITIVE)
    @Size(max = 255, message = "Email must be shorter")
    String email
) {
}
