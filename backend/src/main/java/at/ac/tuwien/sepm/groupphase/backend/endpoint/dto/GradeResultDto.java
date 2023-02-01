package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Station;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;


public record GradeResultDto(
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
    String grade,

    @NotNull(message = "validity must be given")
    Boolean isValid,

    Double result,

    @JsonIgnore
    Station savedStationThatCalculatedResult
) {
    public GradeResultDto withUuid(@NotNull(message = "must have a uuid attached to the message") UUID uuid) {
        return this.uuid == uuid ? this :
            new GradeResultDto(uuid, this.judgeId, this.participantId, this.competitionId, this.gradingGroupId, this.stationId, this.grade, this.isValid,
                this.result, this.savedStationThatCalculatedResult);
    }

    public GradeResultDto withJudgeId(@NotNull(message = "judgeId can't be empty") Long judgeId) {
        return this.judgeId == judgeId ? this :
            new GradeResultDto(this.uuid, judgeId, this.participantId, this.competitionId, this.gradingGroupId, this.stationId, this.grade, this.isValid,
                this.result, this.savedStationThatCalculatedResult);
    }

    public GradeResultDto withParticipantId(@NotNull(message = "participantId must be given") Long participantId) {
        return this.participantId == participantId ? this :
            new GradeResultDto(this.uuid, this.judgeId, participantId, this.competitionId, this.gradingGroupId, this.stationId, this.grade, this.isValid,
                this.result, this.savedStationThatCalculatedResult);
    }

    public GradeResultDto withCompetitionId(@NotNull(message = "competitionId can't be empty") Long competitionId) {
        return this.competitionId == competitionId ? this :
            new GradeResultDto(this.uuid, this.judgeId, this.participantId, competitionId, this.gradingGroupId, this.stationId, this.grade, this.isValid,
                this.result, this.savedStationThatCalculatedResult);
    }

    public GradeResultDto withGradingGroupId(@NotNull(message = "gradingGroupId must be given") Long gradingGroupId) {
        return this.gradingGroupId == gradingGroupId ? this :
            new GradeResultDto(this.uuid, this.judgeId, this.participantId, this.competitionId, gradingGroupId, this.stationId, this.grade, this.isValid,
                this.result, this.savedStationThatCalculatedResult);
    }

    public GradeResultDto withStationId(@NotNull(message = "stationId must be given") Long stationId) {
        return this.stationId == stationId ? this :
            new GradeResultDto(this.uuid, this.judgeId, this.participantId, this.competitionId, this.gradingGroupId, stationId, this.grade, this.isValid,
                this.result, this.savedStationThatCalculatedResult);
    }

    public GradeResultDto withGrade(@Size(max = 4096, message = "grade to long") @NotNull(message = "grade must be given") String grade) {
        return this.grade == grade ? this :
            new GradeResultDto(this.uuid, this.judgeId, this.participantId, this.competitionId, this.gradingGroupId, this.stationId, grade, this.isValid,
                this.result, this.savedStationThatCalculatedResult);
    }

    public GradeResultDto withIsValid(@NotNull(message = "validity must be given") Boolean isValid) {
        return this.isValid == isValid ? this :
            new GradeResultDto(this.uuid, this.judgeId, this.participantId, this.competitionId, this.gradingGroupId, this.stationId, this.grade, isValid,
                this.result, this.savedStationThatCalculatedResult);
    }

    public GradeResultDto withResult(Double result) {
        return this.result == result ? this :
            new GradeResultDto(this.uuid, this.judgeId, this.participantId, this.competitionId, this.gradingGroupId, this.stationId, this.grade, this.isValid,
                result, this.savedStationThatCalculatedResult);
    }

    public GradeResultDto withSavedStationThatCalculatedResult(Station savedStationThatCalculatedResult) {
        return this.savedStationThatCalculatedResult == savedStationThatCalculatedResult ? this :
            new GradeResultDto(this.uuid, this.judgeId, this.participantId, this.competitionId, this.gradingGroupId, this.stationId, this.grade, this.isValid,
                this.result, savedStationThatCalculatedResult);
    }
}
