package at.ac.tuwien.sepm.groupphase.backend.report.ranking;

import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.report.Report;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GradingGroupRankingResults extends IdentifiableRankingEntity {
    public GradingGroupRankingResults(Long id, String name) {
        super(id, name);
    }

    public GradingGroupRankingResults() {

    }

    private final List<Map.Entry<Long, ParticipantRankingResults>> participantsRankingResults = new ArrayList<>();
}
