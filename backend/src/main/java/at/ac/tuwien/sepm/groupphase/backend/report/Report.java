package at.ac.tuwien.sepm.groupphase.backend.report;

import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.report.ranking.GradingGroupRankingResults;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Report {
    @JsonIgnore
    private final HashMap<Long, GradingGroupResults> gradingGroupsResults = new HashMap<>();
    private final List<GradingGroupRankingResults> gradingGroupRankingResults = new ArrayList<>();

    public static Report reportFromGradingGroupRankingResultsJson(String json) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Report newReport = new Report();
            newReport.gradingGroupRankingResults.add(mapper.readValue(json, GradingGroupRankingResults.class));

            return newReport;
        } catch (JsonProcessingException e) {
            throw new ValidationListException(
                "JSON-Report parsing error",
                e.getMessage());
        }
    }

    public void addGradingGroupRankingResultsAsJson(String json) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            gradingGroupRankingResults.add(mapper.readValue(json, GradingGroupRankingResults.class));
        } catch (JsonProcessingException e) {
            throw new ValidationListException(
                "JSON-Report parsing error",
                e.getMessage());
        }
    }

    /*

        {
            gradingGroupId: number;
            gradingGroupName: string;
            participantsRankings: [
                1: {
                    participantId: number;
                    participantName: string;
                    finalResults: number;
                    stationsResults: [
                        {
                            stationId: number;
                            stationName: string;
                            stationResults: number;
                            variableResults: [
                                {
                                    variableId: number;
                                    variableName: string;
                                    variableResult: number;
                                }
                            ]
                        }
                    ]
                },
                1: {},
                2: {},
            ]
        }

     */

    public void putStationResult(
        Long gradingGroupId,
        Long participantId,
        Long stationId,
        GradableEntityInfo gradableEntityInfo
    ) {
        var foundGradingGroupResults = gradingGroupsResults.get(gradingGroupId);
        if (foundGradingGroupResults == null) {
            foundGradingGroupResults = new GradingGroupResults();
            gradingGroupsResults.put(gradingGroupId, foundGradingGroupResults);
        }

        foundGradingGroupResults.putStationResult(participantId, stationId, gradableEntityInfo);
    }

    public void putParticipantResult(
        Long gradingGroupId,
        Long participantId,
        GradableEntityInfo gradableEntityInfo
    ) {
        var foundGradingGroupResults = gradingGroupsResults.get(gradingGroupId);
        if (foundGradingGroupResults == null) {
            foundGradingGroupResults = new GradingGroupResults();
            gradingGroupsResults.put(gradingGroupId, foundGradingGroupResults);
        }

        foundGradingGroupResults.putParticipantResult(participantId, gradableEntityInfo);
    }

    public void putGradeResult(
        Long gradingGroupId,
        Long participantId,
        Long stationId,
        Long gradeId,
        GradableEntityInfo gradableEntityInfo
    ) {
        var foundGradingGroupResults = gradingGroupsResults.get(gradingGroupId);
        if (foundGradingGroupResults == null) {
            foundGradingGroupResults = new GradingGroupResults();
            gradingGroupsResults.put(gradingGroupId, foundGradingGroupResults);
        }

        foundGradingGroupResults.putGradeResult(participantId, stationId, gradeId, gradableEntityInfo);
    }

    public Iterable<Long> getGradingGroupIds() {
        return gradingGroupsResults.keySet();
    }

    public Iterable<Long> getParticipantIds(Long gradingGroupId) {
        return gradingGroupsResults.get(gradingGroupId).getParticipantIds();
    }

    public Set<Map.Entry<Long, GradingGroupResults>> entrySet() {
        return gradingGroupsResults.entrySet();
    }

    public void generateRankings() {
        for (var gradingGroupResultsEntry : gradingGroupsResults.entrySet()) {
            gradingGroupRankingResults.add(
                gradingGroupResultsEntry.getValue().generateRankings(gradingGroupResultsEntry.getKey())
            );
        }
    }

    public HashMap<Long, GradingGroupResults> getGradingGroupsResults() {
        return this.gradingGroupsResults;
    }

    public List<GradingGroupRankingResults> getGradingGroupRankingResults() {
        return this.gradingGroupRankingResults;
    }
}
