package at.ac.tuwien.sepm.groupphase.backend.report.ranking;

import java.util.ArrayList;
import java.util.List;

public class StationRankingResults extends GradedIdentifiableRankingEntity {
    private final List<GradeRankingResults> gradesRankingResults = new ArrayList<>();

    public StationRankingResults(Long id, String name, Double results) {
        super(id, name, results);
    }

    public StationRankingResults() {
    }

    public List<GradeRankingResults> getGradesRankingResults() {
        return this.gradesRankingResults;
    }
}
