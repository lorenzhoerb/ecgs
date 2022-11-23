package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
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

    @Column(nullable = false)
    private LocalDateTime begin;

    @Column(nullable = false)
    private LocalDateTime endOfRegistration;

    @Column(nullable = false)
    private LocalDateTime end;

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
    private Set<Competition> competition;

    public Competition() {}
}
