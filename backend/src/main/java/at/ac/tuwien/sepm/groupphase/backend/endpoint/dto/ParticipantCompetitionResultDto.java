package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.report.ranking.StationRankingResults;
import lombok.With;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;


@With
public record ParticipantCompetitionResultDto(
    @NotNull(message = "participantId must be given")
    Long participantId,

    CompetitionViewDto competition,


    String gradingGroupName,

    Long place,

    Double overAllPoints,

    List<StationRankingResults> stationRankingResults
) {
}
