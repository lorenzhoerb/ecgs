package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserCredentialUpdateDto {

    @NotNull(message = "Email must not be null")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,63}",
        flags = Pattern.Flag.CASE_INSENSITIVE)
    @NotEmpty
    private String email;

    @NotNull(message = "password must not be null")
    @Size(min = 8, max = 256, message = "password must be between 8 and 256 characters long")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserCredentialUpdateDto() {
    }

    public UserCredentialUpdateDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}