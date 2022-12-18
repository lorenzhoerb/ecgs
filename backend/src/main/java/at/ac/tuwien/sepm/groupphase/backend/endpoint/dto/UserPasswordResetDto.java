package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserPasswordResetDto {

    @NotNull(message = "token must not be null")
    @Size(min = 32, max = 32, message = "token must be exactly 32 characters long")
    private String token;

    @NotNull(message = "password must not be null")
    @Size(min = 8, max = 256, message = "password must be between 8 and 256 characters long")
    private String password;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserPasswordResetDto() {
    }

    public UserPasswordResetDto(String token, String password) {
        this.token = token;
        this.password = password;
    }
}
