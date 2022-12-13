package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class UserPasswordResetRequestDto {
    @NotNull(message = "Email must not be null")
    @Email
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
