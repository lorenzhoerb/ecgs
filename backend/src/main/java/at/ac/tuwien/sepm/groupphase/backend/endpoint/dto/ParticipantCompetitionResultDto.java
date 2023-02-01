package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.report.ranking.StationRankingResults;

import javax.validation.constraints.NotNull;
import java.util.List;


public record ParticipantCompetitionResultDto(
    @NotNull(message = "participantId must be given")
    Long participantId,

    CompetitionViewDto competition,


    String gradingGroupName,

    Long place,

    Double overAllPoints,

    List<StationRankingResults> stationRankingResults
) {
    public ParticipantCompetitionResultDto withParticipantId(@NotNull(message = "participantId must be given") Long participantId) {
        return this.participantId == participantId ? this : new ParticipantCompetitionResultDto(participantId, this.competition, this.gradingGroupName, this.place, this.overAllPoints, this.stationRankingResults);
    }

    public ParticipantCompetitionResultDto withCompetition(CompetitionViewDto competition) {
        return this.competition == competition ? this : new ParticipantCompetitionResultDto(this.participantId, competition, this.gradingGroupName, this.place, this.overAllPoints, this.stationRankingResults);
    }

    public ParticipantCompetitionResultDto withGradingGroupName(String gradingGroupName) {
        return this.gradingGroupName == gradingGroupName ? this : new ParticipantCompetitionResultDto(this.participantId, this.competition, gradingGroupName, this.place, this.overAllPoints, this.stationRankingResults);
    }

    public ParticipantCompetitionResultDto withPlace(Long place) {
        return this.place == place ? this : new ParticipantCompetitionResultDto(this.participantId, this.competition, this.gradingGroupName, place, this.overAllPoints, this.stationRankingResults);
    }

    public ParticipantCompetitionResultDto withOverAllPoints(Double overAllPoints) {
        return this.overAllPoints == overAllPoints ? this : new ParticipantCompetitionResultDto(this.participantId, this.competition, this.gradingGroupName, this.place, overAllPoints, this.stationRankingResults);
    }

    public ParticipantCompetitionResultDto withStationRankingResults(List<StationRankingResults> stationRankingResults) {
        return this.stationRankingResults == stationRankingResults ? this : new ParticipantCompetitionResultDto(this.participantId, this.competition, this.gradingGroupName, this.place, this.overAllPoints, stationRankingResults);
    }
}
