package at.ac.tuwien.sepm.groupphase.backend.entity.grade;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

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

    public Grade(GradePk gradePk, ApplicationUser judge, ApplicationUser participant, Competition competition, GradingGroup gradingGroup, String grading, boolean valid) {
        this.gradePk = gradePk;
        this.judge = judge;
        this.participant = participant;
        this.competition = competition;
        this.gradingGroup = gradingGroup;
        this.grading = grading;
        this.valid = valid;
    }

    public Grade() {
    }

    public GradePk getGradePk() {
        return this.gradePk;
    }

    public ApplicationUser getJudge() {
        return this.judge;
    }

    public ApplicationUser getParticipant() {
        return this.participant;
    }

    public Competition getCompetition() {
        return this.competition;
    }

    public GradingGroup getGradingGroup() {
        return this.gradingGroup;
    }

    public String getGrading() {
        return this.grading;
    }

    public boolean isValid() {
        return this.valid;
    }

    public Grade withGradePk(GradePk gradePk) {
        return this.gradePk == gradePk ? this : new Grade(gradePk, this.judge, this.participant, this.competition, this.gradingGroup, this.grading, this.valid);
    }

    public Grade withJudge(ApplicationUser judge) {
        return this.judge == judge ? this : new Grade(this.gradePk, judge, this.participant, this.competition, this.gradingGroup, this.grading, this.valid);
    }

    public Grade withParticipant(ApplicationUser participant) {
        return this.participant == participant ? this : new Grade(this.gradePk, this.judge, participant, this.competition, this.gradingGroup, this.grading, this.valid);
    }

    public Grade withCompetition(Competition competition) {
        return this.competition == competition ? this : new Grade(this.gradePk, this.judge, this.participant, competition, this.gradingGroup, this.grading, this.valid);
    }

    public Grade withGradingGroup(GradingGroup gradingGroup) {
        return this.gradingGroup == gradingGroup ? this : new Grade(this.gradePk, this.judge, this.participant, this.competition, gradingGroup, this.grading, this.valid);
    }

    public Grade withGrading(String grading) {
        return this.grading == grading ? this : new Grade(this.gradePk, this.judge, this.participant, this.competition, this.gradingGroup, grading, this.valid);
    }

    public Grade withValid(boolean valid) {
        return this.valid == valid ? this : new Grade(this.gradePk, this.judge, this.participant, this.competition, this.gradingGroup, this.grading, valid);
    }

    public void setGradePk(GradePk gradePk) {
        this.gradePk = gradePk;
    }

    public void setJudge(ApplicationUser judge) {
        this.judge = judge;
    }

    public void setParticipant(ApplicationUser participant) {
        this.participant = participant;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public void setGradingGroup(GradingGroup gradingGroup) {
        this.gradingGroup = gradingGroup;
    }

    public void setGrading(String grading) {
        this.grading = grading;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Grade)) {
            return false;
        }
        final Grade other = (Grade) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$gradePk = this.getGradePk();
        final Object other$gradePk = other.getGradePk();
        if (this$gradePk == null ? other$gradePk != null : !this$gradePk.equals(other$gradePk)) {
            return false;
        }
        final Object this$judge = this.getJudge();
        final Object other$judge = other.getJudge();
        if (this$judge == null ? other$judge != null : !this$judge.equals(other$judge)) {
            return false;
        }
        final Object this$participant = this.getParticipant();
        final Object other$participant = other.getParticipant();
        if (this$participant == null ? other$participant != null : !this$participant.equals(other$participant)) {
            return false;
        }
        final Object this$competition = this.getCompetition();
        final Object other$competition = other.getCompetition();
        if (this$competition == null ? other$competition != null : !this$competition.equals(other$competition)) {
            return false;
        }
        final Object this$gradingGroup = this.getGradingGroup();
        final Object other$gradingGroup = other.getGradingGroup();
        if (this$gradingGroup == null ? other$gradingGroup != null : !this$gradingGroup.equals(other$gradingGroup)) {
            return false;
        }
        final Object this$grading = this.getGrading();
        final Object other$grading = other.getGrading();
        if (this$grading == null ? other$grading != null : !this$grading.equals(other$grading)) {
            return false;
        }
        if (this.isValid() != other.isValid()) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Grade;
    }

    public int hashCode() {
        final int prime = 59;
        int result = 1;
        final Object $gradePk = this.getGradePk();
        result = result * prime + ($gradePk == null ? 43 : $gradePk.hashCode());
        final Object $judge = this.getJudge();
        result = result * prime + ($judge == null ? 43 : $judge.hashCode());
        final Object $participant = this.getParticipant();
        result = result * prime + ($participant == null ? 43 : $participant.hashCode());
        final Object $competition = this.getCompetition();
        result = result * prime + ($competition == null ? 43 : $competition.hashCode());
        final Object $gradingGroup = this.getGradingGroup();
        result = result * prime + ($gradingGroup == null ? 43 : $gradingGroup.hashCode());
        final Object $grading = this.getGrading();
        result = result * prime + ($grading == null ? 43 : $grading.hashCode());
        result = result * prime + (this.isValid() ? 79 : 97);
        return result;
    }
}