package at.ac.tuwien.sepm.groupphase.backend.report;

import at.ac.tuwien.sepm.groupphase.backend.report.ranking.ParticipantRankingResults;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public class ParticipantResults extends GradableEntityInfo {
    private final HashMap<Long, StationResults> stationsResults = new HashMap<>();

    public void putStationResult(
        Long stationId,
        GradableEntityInfo gradableEntityInfo
    ) {
        var foundStationResults = stationsResults.get(stationId);
        if (foundStationResults == null) {
            foundStationResults = new StationResults();
            stationsResults.put(stationId, foundStationResults);
        }

        if (!gradableEntityInfo.getName().isBlank()) {
            foundStationResults.setName(gradableEntityInfo.getName());
        }
        if (gradableEntityInfo.getResults() != null) {
            foundStationResults.setResults(gradableEntityInfo.getResults());
        }
    }

    public void putGradeResult(
        Long stationId,
        Long gradeId,
        GradableEntityInfo gradableEntityInfo
    ) {
        var foundStationResults = stationsResults.get(stationId);
        if (foundStationResults == null) {
            foundStationResults = new StationResults();
            stationsResults.put(stationId, foundStationResults);
        }

        foundStationResults.putGradeResult(gradeId, gradableEntityInfo);
    }

    public Set<Map.Entry<Long, StationResults>> entrySet() {
        return stationsResults.entrySet();
    }

    public ParticipantRankingResults generateRankings(Long participantId) {
        var newParticipantRankingResults = new ParticipantRankingResults(
            participantId,
            getName(),
            getResults()
        );
        for (var stationResultsEntry : stationsResults.entrySet()) {
            newParticipantRankingResults.getStationsRankingResults().add(
                stationResultsEntry.getValue().generateRankings(stationResultsEntry.getKey())
            );
        }

        return newParticipantRankingResults;
    }
}
