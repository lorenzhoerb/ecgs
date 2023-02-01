package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LiveResultDto;

import java.util.List;

public interface GradeService {
    /**
     * Create or update a grade.
     *
     * @param competitionId  competition Id
     * @param gradingGroupId gradingGroup Id
     * @param stationName    name of the Station
     * @param grade          grade to store for station of gradingGroup in competition
     * @return unchanged GradeDto on success
     */
    GradeResultDto updateCompetitionResults(Long competitionId, Long gradingGroupId, String stationName, GradeDto grade);

    /**
     * gets all results for given participant and station.
     *
     * @param competitionId  the id of the competition
     * @param gradingGroupId the id of the grading group
     * @param stationId      the id of the station
     * @param participantId  the id of the participant
     * @param result         the resulting value for the station
     * @return ResultDto for the Live Grade feature
     */
    LiveResultDto getAllResultsForParticipantAtStation(Long competitionId, Long gradingGroupId, Long stationId, Long participantId, Double result);

    /**
     * Get all results for a given competition.
     *
     * @param competitionId id of the competition
     * @return a list containing all results for the given competition
     */
    List<LiveResultDto> getAllResults(Long competitionId);

    /**
     * ensures that the judge can grade on the given competition and returns the id.
     *
     * @param competitionId  id of the competition
     * @param gradingGroupId id of the gradingGroup
     * @param stationId      if of the station
     * @return the id of the judge
     */
    Long verifyJudgeAndReturnId(Long competitionId, Long gradingGroupId, Long stationId);

    /**
     * Gets all grades for all participants for a station.
     *
     * @param competitionId  the id of the competition
     * @param gradingGroupId the id of the gradingGroup
     * @param stationId      the id of the station
     * @return the results for all participants
     */
    List<GradeResultDto> getAllGradesForStation(Long competitionId, Long gradingGroupId, Long stationId);

    /**
     * Method returning true if the user judges for this competition false otherwise.
     *
     * @param competitionId the id if the competition
     * @return true if the user judges for this competition false otherwise
     */
    boolean userJudges(Long competitionId);
}
