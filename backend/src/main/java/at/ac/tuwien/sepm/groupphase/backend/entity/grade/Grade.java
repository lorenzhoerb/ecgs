package at.ac.tuwien.sepm.groupphase.backend.entity.grade;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.Judge;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import javax.el.MapELResolver;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
@EqualsAndHashCode
@Entity
public class Grade {

    @EmbeddedId
    private GradePk gradePk;

    @ManyToOne
    @MapsId("judgeId")
    @JoinColumn
    private ApplicationUser judge;

    @ManyToOne
    @MapsId("participantId")
    @JoinColumn
    private ApplicationUser participant;

    @ManyToOne
    @MapsId("competitionId")
    @JoinColumn
    private Competition competition;

    @ManyToOne
    @MapsId("gradingGroupId")
    @JoinColumn
    private GradingGroup gradingGroup;

    @Column(nullable = false)
    private String grading;

    @Column(nullable = false)
    private boolean valid;

    public Grade(GradePk gradePk, String grading, boolean valid) {
        this.gradePk = gradePk;
        this.grading = grading;
        this.valid = valid;
    }

    public Grade(ApplicationUser judge, ApplicationUser participant, Competition competition, GradingGroup gradingGroup, String grading, boolean valid) {
        this.judge = judge;
        this.participant = participant;
        this.competition = competition;
        this.gradingGroup = gradingGroup;
        this.grading = grading;
        this.valid = valid;
    }
}