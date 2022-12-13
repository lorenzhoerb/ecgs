package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserCredentialUpdateDto {

    @NotNull(message = "Email must not be null")
    @Email
    private String email;

    @NotNull(message = "password must not be null")
    @Size(min = 8, max = 64, message = "password must be between 8 and 64 characters long")
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