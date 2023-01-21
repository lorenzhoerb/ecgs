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


}