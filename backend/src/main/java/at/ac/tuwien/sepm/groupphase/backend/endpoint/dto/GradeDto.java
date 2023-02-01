package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;


public record GradeDto(
    @NotNull(message = "must have a uuid attached to the message")
    UUID uuid,

    @NotNull(message = "judgeId can't be empty")
    Long judgeId,

    @NotNull(message = "participantId must be given")
    Long participantId,

    @NotNull(message = "competitionId can't be empty")
    Long competitionId,

    @NotNull(message = "gradingGroupId must be given")
    Long gradingGroupId,

    @NotNull(message = "stationId must be given")
    Long stationId,

    @Size(max = 4096, message = "grade to long")
    @NotNull(message = "grade must be given")
    String grade
) {
    public GradeDto withUuid(UUID uuid) {
        return new GradeDto(uuid, this.judgeId, this.participantId, this.competitionId, this.gradingGroupId, this.stationId, this.grade);
    }

    public GradeDto withJudgeId(Long judgeId) {
        return new GradeDto(this.uuid, judgeId, this.participantId, this.competitionId, this.gradingGroupId, this.stationId, this.grade);
    }

    public GradeDto withParticipantId(Long participantId) {
        return new GradeDto(this.uuid, this.judgeId, participantId, this.competitionId, this.gradingGroupId, this.stationId, this.grade);
    }

    public GradeDto withCompetitionId(Long competitionId) {
        return new GradeDto(this.uuid, this.judgeId, this.participantId, competitionId, this.gradingGroupId, this.stationId, this.grade);
    }

    public GradeDto withGradingGroupId(Long gradingGroupId) {
        return new GradeDto(this.uuid, this.judgeId, this.participantId, this.competitionId, gradingGroupId, this.stationId, this.grade);
    }

    public GradeDto withStationId(Long stationId) {
        return new GradeDto(this.uuid, this.judgeId, this.participantId, this.competitionId, this.gradingGroupId, stationId, this.grade);
    }

    public GradeDto withGrade(String grade) {
        return new GradeDto(this.uuid, this.judgeId, this.participantId, this.competitionId, this.gradingGroupId, this.stationId, grade);
    }
}
