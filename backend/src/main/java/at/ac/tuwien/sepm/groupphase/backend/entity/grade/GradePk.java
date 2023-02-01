package at.ac.tuwien.sepm.groupphase.backend.entity.grade;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Embeddable
public class GradePk implements Serializable {
    @NotNull
    private Long judgeId;

    @NotNull
    private Long participantId;

    @NotNull
    private Long competitionId;

    @NotNull
    private Long gradingGroupId;

    @NotNull
    private Long stationId;

    public GradePk(@NotNull Long judgeId, @NotNull Long participantId, @NotNull Long competitionId, @NotNull Long gradingGroupId, @NotNull Long stationId) {
        this.judgeId = judgeId;
        this.participantId = participantId;
        this.competitionId = competitionId;
        this.gradingGroupId = gradingGroupId;
        this.stationId = stationId;
    }

    public GradePk() {
    }

    public @NotNull Long getJudgeId() {
        return this.judgeId;
    }

    public @NotNull Long getParticipantId() {
        return this.participantId;
    }

    public @NotNull Long getCompetitionId() {
        return this.competitionId;
    }

    public @NotNull Long getGradingGroupId() {
        return this.gradingGroupId;
    }

    public @NotNull Long getStationId() {
        return this.stationId;
    }

    public void setJudgeId(@NotNull Long judgeId) {
        this.judgeId = judgeId;
    }

    public void setParticipantId(@NotNull Long participantId) {
        this.participantId = participantId;
    }

    public void setCompetitionId(@NotNull Long competitionId) {
        this.competitionId = competitionId;
    }

    public void setGradingGroupId(@NotNull Long gradingGroupId) {
        this.gradingGroupId = gradingGroupId;
    }

    public void setStationId(@NotNull Long stationId) {
        this.stationId = stationId;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GradePk)) {
            return false;
        }
        final GradePk other = (GradePk) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$judgeId = this.getJudgeId();
        final Object other$judgeId = other.getJudgeId();
        if (this$judgeId == null ? other$judgeId != null : !this$judgeId.equals(other$judgeId)) {
            return false;
        }
        final Object this$participantId = this.getParticipantId();
        final Object other$participantId = other.getParticipantId();
        if (this$participantId == null ? other$participantId != null : !this$participantId.equals(other$participantId)) {
            return false;
        }
        final Object this$competitionId = this.getCompetitionId();
        final Object other$competitionId = other.getCompetitionId();
        if (this$competitionId == null ? other$competitionId != null : !this$competitionId.equals(other$competitionId)) {
            return false;
        }
        final Object this$gradingGroupId = this.getGradingGroupId();
        final Object other$gradingGroupId = other.getGradingGroupId();
        if (this$gradingGroupId == null ? other$gradingGroupId != null : !this$gradingGroupId.equals(other$gradingGroupId)) {
            return false;
        }
        final Object this$stationId = this.getStationId();
        final Object other$stationId = other.getStationId();
        if (this$stationId == null ? other$stationId != null : !this$stationId.equals(other$stationId)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof GradePk;
    }

    public int hashCode() {
        final int prime = 59;
        int result = 1;
        final Object $judgeId = this.getJudgeId();
        result = result * prime + ($judgeId == null ? 43 : $judgeId.hashCode());
        final Object $participantId = this.getParticipantId();
        result = result * prime + ($participantId == null ? 43 : $participantId.hashCode());
        final Object $competitionId = this.getCompetitionId();
        result = result * prime + ($competitionId == null ? 43 : $competitionId.hashCode());
        final Object $gradingGroupId = this.getGradingGroupId();
        result = result * prime + ($gradingGroupId == null ? 43 : $gradingGroupId.hashCode());
        final Object $stationId = this.getStationId();
        result = result * prime + ($stationId == null ? 43 : $stationId.hashCode());
        return result;
    }
}
