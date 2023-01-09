package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UserPasswordResetRequestDto {
    @NotNull(message = "Email must not be null")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,63}",
        flags = Pattern.Flag.CASE_INSENSITIVE)
    @NotEmpty(message = "Email must not be empty")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserPasswordResetRequestDto() {
    }

    public UserPasswordResetRequestDto(String email) {
        this.email = email;
    }
}
