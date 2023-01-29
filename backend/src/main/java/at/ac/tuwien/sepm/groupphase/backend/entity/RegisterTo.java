package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;
import java.io.Serializable;
import java.util.Set;

@Entity
public class RegisterTo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(referencedColumnName = "id")
    private ApplicationUser participant;

    @ManyToOne()
    @JoinColumn(referencedColumnName = "id")
    private GradingGroup gradingGroup;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "registrations")
    private Set<Flags> flags;

    @Column(nullable = false)
    private Boolean accepted;

    public RegisterTo() {
    }

    public RegisterTo(ApplicationUser participant, GradingGroup gradingGroup, Boolean accepted) {
        this.participant = participant;
        this.gradingGroup = gradingGroup;
        this.accepted = accepted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getParticipant() {
        return participant;
    }

    public void setParticipant(ApplicationUser participant) {
        this.participant = participant;
    }

    public GradingGroup getGradingGroup() {
        return gradingGroup;
    }

    public void setGradingGroup(GradingGroup gradingGroup) {
        this.gradingGroup = gradingGroup;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Set<Flags> getFlags() {
        return flags;
    }

    public void setFlags(Set<Flags> flags) {
        this.flags = flags;
    }

    @Override
    public String toString() {
        return "RegisterTo{"
            + "participant=" + participant
            + ", gradingGroup=" + gradingGroup
            + ", accepted=" + accepted
            + '}';
    }
}