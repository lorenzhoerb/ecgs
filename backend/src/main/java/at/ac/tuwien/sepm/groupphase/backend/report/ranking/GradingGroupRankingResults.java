package at.ac.tuwien.sepm.groupphase.backend.report.ranking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GradingGroupRankingResults extends IdentifiableRankingEntity {
    public GradingGroupRankingResults(Long id, String name) {
        super(id, name);
    }

    public GradingGroupRankingResults() {

    }

    private final List<Map.Entry<Long, ParticipantRankingResults>> participantsRankingResults = new ArrayList<>();

    public List<Map.Entry<Long, ParticipantRankingResults>> getParticipantsRankingResults() {
        return this.participantsRankingResults;
    }
}
