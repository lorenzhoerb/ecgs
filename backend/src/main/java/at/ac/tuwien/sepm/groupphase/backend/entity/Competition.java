package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4095)
    private String name;

    // @TODO: change name in the ER-Diagram to match end
    @Column(nullable = false)
    private LocalDateTime beginOfRegistration;

    @Column(nullable = false)
    private LocalDateTime endOfRegistration;

    // @TODO: change name in the ER-Diagram as end is a keyword in h2
    @Column(nullable = false)
    private LocalDateTime endOfCompetition;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 4095)
    private String picturePath;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false)
    private Boolean draft;

    @Column(nullable = true, length = 255)
    private String email;

    @Column(nullable = true, length = 255)
    private String phone;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
        name = "competition_gradingGroup",
        joinColumns = {@JoinColumn(referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(referencedColumnName = "id")}
    )
    private Set<GradingGroup> competition;

    // @TODO: adjust ER-Diagram relation unneeded
    /*
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private ApplicationUser participant;
    */

    @OneToMany(mappedBy = "competition")
    private Set<Judge> judges;

    public Competition() {}
}
