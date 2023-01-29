package at.ac.tuwien.sepm.groupphase.backend.report;

import at.ac.tuwien.sepm.groupphase.backend.report.ranking.GradedIdentifiableRankingEntity;
import at.ac.tuwien.sepm.groupphase.backend.report.ranking.GradingGroupRankingResults;
import at.ac.tuwien.sepm.groupphase.backend.report.ranking.ParticipantRankingResults;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import javax.mail.Part;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class GradingGroupResults {
    private String name;

    private final HashMap<Long, ParticipantResults> participantsResults = new HashMap<>();

    public void setName(String name) {
        this.name = name;
    }

    public void putStationResult(
        Long participantId,
        Long stationId,
        GradableEntityInfo gradableEntityInfo
    ) {
        var foundParticipantResults = participantsResults.get(participantId);
        if (foundParticipantResults == null) {
            foundParticipantResults = new ParticipantResults();
            participantsResults.put(participantId, foundParticipantResults);
        }

        foundParticipantResults.putStationResult(stationId, gradableEntityInfo);
    }

    public void putParticipantResult(
        Long gradingGroupId,
        GradableEntityInfo gradableEntityInfo
    ) {
        var foundParticipantResults = participantsResults.get(gradingGroupId);
        if (foundParticipantResults == null) {
            foundParticipantResults = new ParticipantResults();
            participantsResults.put(gradingGroupId, foundParticipantResults);
        }
        if (!gradableEntityInfo.getName().isBlank()) {
            foundParticipantResults.setName(gradableEntityInfo.getName());
        }
        if (gradableEntityInfo.getResults() != null) {
            foundParticipantResults.setResults(gradableEntityInfo.getResults());
        }
    }

    public void putGradeResult(
        Long participantId,
        Long stationId,
        Long gradeId,
        GradableEntityInfo gradableEntityInfo
    ) {
        var foundParticipantResults = participantsResults.get(participantId);
        if (foundParticipantResults == null) {
            foundParticipantResults = new ParticipantResults();
            participantsResults.put(participantId, foundParticipantResults);
        }

        foundParticipantResults.putGradeResult(stationId, gradeId, gradableEntityInfo);
    }

    public Set<Map.Entry<Long, ParticipantResults>> entrySet() {
        return participantsResults.entrySet();
    }

    @JsonIgnore
    public Iterable<Long> getParticipantIds() {
        return participantsResults.keySet();
    }

    public GradingGroupRankingResults generateRankings(Long gradingGroupId) {
        var newGradingGroupRankingResults = new GradingGroupRankingResults(
            gradingGroupId,
            getName()
        );
        List<ParticipantRankingResults> newParticipantsRankings = new ArrayList<>();
        for (var participantResultEntry : participantsResults.entrySet()) {
            newParticipantsRankings.add(new ParticipantRankingResults(
                participantResultEntry.getKey(),
                participantResultEntry.getValue().getName(),
                participantResultEntry.getValue().getResults()
            ));
        }
        var newParticipantsRankingsSorted = newParticipantsRankings.stream()
            .sorted(Comparator.comparingDouble(ParticipantRankingResults::getResults).reversed())
            .toList();

        double lastResultSeen = Double.MAX_VALUE;
        long participantRank = 1L;
        long participantCount = 0L;
        for (var participantResultsEntry : newParticipantsRankingsSorted) {
            participantCount++;
            double currentResult = participantResultsEntry.getResults();
            if (currentResult < lastResultSeen) {
                participantRank = participantCount;
                lastResultSeen = currentResult;
            }
            newGradingGroupRankingResults.getParticipantsRankingResults().add(Map.entry(
                    participantRank,
                    participantsResults.get(participantResultsEntry.getId())
                        .generateRankings(participantResultsEntry.getId())
                )
            );
        }

        return newGradingGroupRankingResults;
    }
}
