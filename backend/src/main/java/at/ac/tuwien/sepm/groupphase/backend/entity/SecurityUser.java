package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
public class SecurityUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 4, max = 64, message = "email must be between 4 and 64 characters long")
    @Column(nullable = false, length = 64, unique = true)
    private String email;

    @Size(min = 8, max = 64, message = "password must be between 8 and 64 characters long")
    @Column(nullable = false, length = 64)
    private String password;

    @Size(min = 32, max = 32, message = "resetToken must be exactly 32 characters long")
    @Column()
    private String resetPasswordToken;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private ApplicationUser user;

    public SecurityUser() {
    }

    public SecurityUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    @Override
    public String toString() {
        return "SecurityUser{"
            + "id=" + id
            + ", email='" + email + '\''
            + ", password='" + password + '\''
            + ", user=" + user
            + '}';
    }
}
