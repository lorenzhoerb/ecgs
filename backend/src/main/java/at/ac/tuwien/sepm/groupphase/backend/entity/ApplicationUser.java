package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ApplicationUser {

    enum Gender {
        MALE,
        FEMALE,
        OTHER
    };

    enum Role {
        Participant,
        ClubManager,
        TournamentManager
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private User user;

    @Column(nullable=false)
    private Role type;

    @Column(nullable=false, length=255)
    private String first_name;

    @Column(nullable=false, length=255)
    private String last_name;

    @Column(nullable=false)
    private Gender gender;

    @Column(nullable=false)
    private Date date_of_birth;

    @Column(nullable=false, length=4095)
    private String picture_path;

    public ApplicationUser() {
    }

    public ApplicationUser(User user, Role type, String first_name, String last_name, Gender gender,
                           Date date_of_birth, String picture_path) {
        this.user = user;
        this.type = type;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.date_of_birth = date_of_birth;
        this.picture_path = picture_path;
    }

    public String getEmail() {
        return user.getEmail();
    }

    public void setEmail(String email) {
        user.setEmail(email);
    }

    public String getPassword() {
        return user.getPassword();
    }

    public void setPassword(String password) {
        user.setPassword(password);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getType() {
        return type;
    }

    public void setType(Role type) {
        this.type = type;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getPicture_path() {
        return picture_path;
    }

    public void setPicture_path(String picture_path) {
        this.picture_path = picture_path;
    }
}
