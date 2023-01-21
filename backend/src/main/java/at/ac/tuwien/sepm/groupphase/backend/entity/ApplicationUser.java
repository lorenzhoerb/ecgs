package at.ac.tuwien.sepm.groupphase.backend.entity;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Entity
public class ApplicationUser {

    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

    public enum Role {
        PARTICIPANT,
        CLUB_MANAGER,
        TOURNAMENT_MANAGER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private SecurityUser user;

    @Column(nullable = false)
    private Role type;

    @Size(min = 2, max = 32, message = "first name must be between 2 and 32 characters long")
    @Pattern(regexp = "^[a-zA-Z_.\\-]+$", message = "first name can only include letters and .-_")
    @Column(nullable = false, length = 32, unique = false)
    private String firstName;

    @Size(min = 2, max = 32, message = "last name must be between 2 and 32 characters long")
    @Pattern(regexp = "^[a-zA-Z_.\\-]+$", message = "last name can only include letters and .-_")
    @Column(nullable = false, length = 32, unique = false)
    private String lastName;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Date dateOfBirth;

    @Column(length = 4095)
    private String picturePath;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "manager")
    private Set<ManagedBy> members;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "member")
    private Set<ManagedBy> managers;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creator", fetch = FetchType.EAGER)
    private Set<Competition> competitions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "judge")
    private Set<Judge> judges;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "participant")
    private Set<RegisterTo> registrations;

    public ApplicationUser() {
    }

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "judges")
    private Set<Competition> judging;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creator")
    private Set<GradingSystem> gradingSystems;

    public ApplicationUser(Role type, String firstName, String lastName, Gender gender,
                           Date dateOfBirth, String picturePath) {
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

    public Set<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(Set<Competition> competitions) {
        this.competitions = competitions;
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

    public Set<Competition> getJudging() {
        return judging;
    }

    public void setJudging(Set<Competition> judging) {
        this.judging = judging;
    }

    public Set<GradingSystem> getGradingSystems() {
        return gradingSystems;
    }

    public void setGradingSystems(Set<GradingSystem> gradingSystems) {
        this.gradingSystems = gradingSystems;
    }

    @Override
    public String toString() {
        return "ApplicationUser{"
            + "id=" + id
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
