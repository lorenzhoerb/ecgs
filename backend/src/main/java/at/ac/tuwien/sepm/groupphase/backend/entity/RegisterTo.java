package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;
import java.io.Serializable;

@Entity
public class RegisterTo implements Serializable {

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private ApplicationUser participant;

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private GradingGroup gradingGroup;

    // @NOTICE: The relationships to Grade and Flags from the ER-Diagram are not modeled her
    // as they do not really make sense.

    @Column(nullable = false)
    private Boolean accepted;

    public RegisterTo() {}

    public RegisterTo(ApplicationUser participant, GradingGroup gradingGroup, Boolean accepted) {
        this.participant = participant;
        this.gradingGroup = gradingGroup;
        this.accepted = accepted;
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

    @Override
    public String toString() {
        return "RegisterTo{"
            + "participant=" + participant
            + ", gradingGroup=" + gradingGroup
            + ", accepted=" + accepted
            + '}';
    }
}
