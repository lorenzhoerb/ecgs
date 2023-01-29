package at.ac.tuwien.sepm.groupphase.backend.report;

import at.ac.tuwien.sepm.groupphase.backend.report.ranking.GradeRankingResults;
import at.ac.tuwien.sepm.groupphase.backend.report.ranking.StationRankingResults;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public class StationResults extends GradableEntityInfo {
    private final HashMap<Long, GradableEntityInfo> gradesResults = new HashMap<>();

    public void putGradeResult(
        Long gradeId,
        GradableEntityInfo gradableEntityInfo
    ) {
        var foundGradeResults = gradesResults.get(gradeId);
        if (foundGradeResults == null) {
            gradesResults.put(gradeId, gradableEntityInfo);
        } else {
            if (!gradableEntityInfo.getName().isBlank()) {
                foundGradeResults.setName(gradableEntityInfo.getName());
            }
            if (gradableEntityInfo.getResults() != null) {
                foundGradeResults.setResults(gradableEntityInfo.getResults());
            }
        }
    }

    public Set<Map.Entry<Long, GradableEntityInfo>> entrySet() {
        return gradesResults.entrySet();
    }

    public StationRankingResults generateRankings(Long stationId) {
        var newStationRankingResults = new StationRankingResults(
            stationId,
            getName(),
            getResults()
        );
        for (var gradeResultEntry : gradesResults.entrySet()) {
            newStationRankingResults.getGradesRankingResults().add(
                new GradeRankingResults(
                    gradeResultEntry.getKey(),
                    gradeResultEntry.getValue().getName(),
                    gradeResultEntry.getValue().getResults()
                )
            );
        }

        return newStationRankingResults;
    }
}
