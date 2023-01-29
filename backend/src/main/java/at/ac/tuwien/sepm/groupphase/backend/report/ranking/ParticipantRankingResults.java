package at.ac.tuwien.sepm.groupphase.backend.report.ranking;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ParticipantRankingResults extends GradedIdentifiableRankingEntity {
    private final List<StationRankingResults> stationsRankingResults = new ArrayList<>();

    public ParticipantRankingResults() {
    }

    public ParticipantRankingResults(Long id, String name, Double results) {
        super(id, name, results);
    }
}
