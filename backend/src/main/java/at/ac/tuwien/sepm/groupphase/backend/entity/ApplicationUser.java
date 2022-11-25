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

    @OneToMany(mappedBy = "manager")
    private Set<ManagedBy> members;

    @OneToMany(mappedBy = "member")
    private Set<ManagedBy> managers;

    // @TODO: adjust ER-Diagram relation unneeded
    /*
    @OneToMany(mappedBy = "participant")
    private Set<Competition> competitions;
     */

    @OneToMany(mappedBy = "participant")
    private Set<Judge> judges;

    @OneToMany(mappedBy = "participant")
    private Set<RegisterTo> registrations;

    public ApplicationUser() {
    }
}
