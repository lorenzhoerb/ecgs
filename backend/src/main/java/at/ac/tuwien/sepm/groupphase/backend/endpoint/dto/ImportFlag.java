package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ImportFlag {
    @Override
    public String toString() {
        return "ImportFlag{"
            + "email='" + email + '\''
            + ", flag='" + flag + '\''
            + '}';
    }

    @NotBlank(message = "Email must be specified")
    @Email(message = "Not valid email specified")
    @Size(max = 255, message = "Email is too long")
    String email;
    @NotBlank(message = "Flag must be specified")
    @Size(max = 255, message = "Flag is too long")
    String flag;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public ImportFlag(String email, String flag) {
        this.email = email;
        this.flag = flag;
    }

    public ImportFlag() {

    }
}
