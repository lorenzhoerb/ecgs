package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import java.util.Date;
import java.util.Set;

@Entity
public class ApplicationUser {

    enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

    enum Role {
        Participant,
        ClubManager,
        TournamentManager
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private SecurityUser user;

    @Column(nullable = false)
    private Role type;

    @Column(nullable = false, length = 255)
    private String firstName;

    @Column(nullable = false, length = 255)
    private String lastName;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Date dateOfBirth;

    @Column(nullable = false, length = 4095)
    private String picturePath;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "manager")
    private Set<ManagedBy> members;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "member")
    private Set<ManagedBy> managers;

    // @TODO: adjust ER-Diagram relation unneeded
    /*
    @OneToMany(mappedBy = "participant")
    private Set<Competition> competitions;
     */

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "participant")
    private Set<Judge> judges;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "participant")
    private Set<RegisterTo> registrations;

    public ApplicationUser() {
    }

    public ApplicationUser(SecurityUser user, Role type, String firstName, String lastName, Gender gender,
                           Date dateOfBirth, String picturePath) {
        this.user = user;
        this.type = type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.picturePath = picturePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SecurityUser getUser() {
        return user;
    }

    public void setUser(SecurityUser user) {
        this.user = user;
    }

    public Role getType() {
        return type;
    }

    public void setType(Role type) {
        this.type = type;
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public Set<ManagedBy> getMembers() {
        return members;
    }

    public void setMembers(Set<ManagedBy> members) {
        this.members = members;
    }

    public Set<ManagedBy> getManagers() {
        return managers;
    }

    public void setManagers(Set<ManagedBy> managers) {
        this.managers = managers;
    }

    public Set<Judge> getJudges() {
        return judges;
    }

    public void setJudges(Set<Judge> judges) {
        this.judges = judges;
    }

    public Set<RegisterTo> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(Set<RegisterTo> registrations) {
        this.registrations = registrations;
    }

    @Override
    public String toString() {
        return "ApplicationUser{"
            + "id=" + id
            + ", user=" + user
            + ", type=" + type
            + ", firstName='" + firstName + '\''
            + ", lastName='" + lastName + '\''
            + ", gender=" + gender
            + ", dateOfBirth=" + dateOfBirth
            + ", picturePath='" + picturePath + '\''
            + ", members=" + members
            + ", managers=" + managers
            + ", judges=" + judges
            + ", registrations=" + registrations
            + '}';
    }
}
