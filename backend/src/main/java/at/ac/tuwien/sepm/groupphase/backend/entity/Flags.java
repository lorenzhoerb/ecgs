package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
public class Flags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
        name = "flag_managedBy",
        joinColumns = {@JoinColumn(referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(referencedColumnName = "id")}
    )
    private Set<ManagedBy> clubs;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
        name = "flag_registerTo",
        joinColumns = {@JoinColumn(referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(referencedColumnName = "id")}
    )
    private Set<RegisterTo> registrations;

    public Flags() {
    }

    public Flags(String name) {
        this.name = name;
    }

    public Flags(String name, Set<ManagedBy> clubs) {
        this.name = name;
        this.clubs = clubs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ManagedBy> getClubs() {
        return clubs;
    }

    public void setClubs(Set<ManagedBy> clubs) {
        this.clubs = clubs;
    }

    public Set<RegisterTo> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(Set<RegisterTo> registrations) {
        this.registrations = registrations;
    }

    @Override
    public String toString() {
        return "Flags{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", clubs=" + clubs
            + '}';
    }
}
