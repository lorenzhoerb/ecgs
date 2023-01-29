package at.ac.tuwien.sepm.groupphase.backend.report.ranking;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class StationRankingResults extends GradedIdentifiableRankingEntity {
    private final List<GradeRankingResults> gradesRankingResults = new ArrayList<>();

    public StationRankingResults(Long id, String name, Double results) {
        super(id, name, results);
    }

    public StationRankingResults() {
    }
}
