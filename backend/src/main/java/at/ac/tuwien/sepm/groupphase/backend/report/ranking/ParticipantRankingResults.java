package at.ac.tuwien.sepm.groupphase.backend.report.ranking;

import java.util.ArrayList;
import java.util.List;

public class ParticipantRankingResults extends GradedIdentifiableRankingEntity {
    private final List<StationRankingResults> stationsRankingResults = new ArrayList<>();

    public ParticipantRankingResults() {
    }

    public ParticipantRankingResults(Long id, String name, Double results) {
        super(id, name, results);
    }

    public List<StationRankingResults> getStationsRankingResults() {
        return this.stationsRankingResults;
    }
}
