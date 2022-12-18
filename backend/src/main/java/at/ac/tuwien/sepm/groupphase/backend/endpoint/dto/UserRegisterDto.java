package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

public class UserRegisterDto {

    @NotNull(message = "Email must not be null")
    @Email
    private String email;

    @NotNull(message = "password must not be null")
    @Size(min = 8, max = 256, message = "password must be between 8 and 256 characters long")
    private String password;

    @Size(min = 2, max = 32, message = "firstName must be between 2 and 32 characters long")
    @Pattern(regexp = "^[a-zA-Z_.\\-]+$", message = "firstName can only contain letters and .-_ ")
    private String firstName;

    @Size(min = 2, max = 32, message = "lastName must be between 2 and 32 characters long")
    @Pattern(regexp = "^[a-zA-Z_.\\-]+$", message = "lastName can only contain letters and .-_ ")
    private String lastName;

    @NotNull(message = "Gender must not be null")
    private ApplicationUser.Gender gender;

    @NotNull(message = "Date of Birth must not be null")
    @Past
    private Date dateOfBirth;

    @NotNull(message = "Type must not be null")
    private ApplicationUser.Role type;

    public UserRegisterDto() {
    }

    public UserRegisterDto(String email, String password, String firstName, String lastName, ApplicationUser.Gender gender, Date dateOfBirth,
                           ApplicationUser.Role type) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.type = type;
    }

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ApplicationUser.Gender getGender() {
        return gender;
    }

    public void setGender(ApplicationUser.Gender gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public ApplicationUser.Role getType() {
        return type;
    }

    public void setType(ApplicationUser.Role type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserRegisterDto that = (UserRegisterDto) o;

        if (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null) {
            return false;
        }
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null) {
            return false;
        }
        if (getFirstName() != null ? !getFirstName().equals(that.getFirstName()) : that.getFirstName() != null) {
            return false;
        }
        if (getLastName() != null ? !getLastName().equals(that.getLastName()) : that.getLastName() != null) {
            return false;
        }
        if (getGender() != that.getGender()) {
            return false;
        }
        if (getDateOfBirth() != null ? !getDateOfBirth().equals(that.getDateOfBirth()) : that.getDateOfBirth() != null) {
            return false;
        }
        return getType() == that.getType();
    }

    @Override
    public int hashCode() {
        int result = getEmail() != null ? getEmail().hashCode() : 0;
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getFirstName() != null ? getFirstName().hashCode() : 0);
        result = 31 * result + (getLastName() != null ? getLastName().hashCode() : 0);
        result = 31 * result + (getGender() != null ? getGender().hashCode() : 0);
        result = 31 * result + (getDateOfBirth() != null ? getDateOfBirth().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserRegisterDto{"
            + "email='" + email + '\''
            + ", password='" + password + '\''
            + ", firstName='" + firstName + '\''
            + ", lastName='" + lastName + '\''
            + ", gender=" + gender
            + ", dateOfBirth=" + dateOfBirth
            + ", type=" + type
            + '}';
    }


    public static final class UserRegisterDtoBuilder {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private ApplicationUser.Gender gender;
        private Date dateOfBirth;
        private ApplicationUser.Role type;

        public UserRegisterDtoBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public UserRegisterDtoBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public UserRegisterDtoBuilder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserRegisterDtoBuilder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserRegisterDtoBuilder setGender(ApplicationUser.Gender gender) {
            this.gender = gender;
            return this;
        }

        public UserRegisterDtoBuilder setDateOfBirth(Date dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public UserRegisterDtoBuilder setType(ApplicationUser.Role type) {
            this.type = type;
            return this;
        }

        public UserRegisterDto createUserRegisterDto() {
            return new UserRegisterDto(email, password, firstName, lastName, gender, dateOfBirth, type);
        }
    }


}
