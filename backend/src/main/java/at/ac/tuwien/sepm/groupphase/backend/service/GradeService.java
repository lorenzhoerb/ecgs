package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportDownloadResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportGenerationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LiveResultDto;

import java.util.List;

public interface GradeService {
    /**
     * Create or update a grade.
     *
     * @param competitionId competition Id
     * @param gradingGroupId gradingGroup Id
     * @param stationName name of the Station
     * @param grade grade to store for station of gradingGroup in competition
     * @return unchanged GradeDto on success
     */
    GradeResultDto updateCompetitionResults(Long competitionId, Long gradingGroupId, String stationName, GradeDto grade);

    LiveResultDto getAllResultsForParticipantAtStation(Long competitionId, Long gradingGroupId, Long stationId, Long participantId, Double result);

    List<LiveResultDto> getAllResults(Long competitionId);

    Long verifyJudgeAndReturnId(Long competitionId, Long gradingGroupId, Long stationId);

    List<GradeResultDto> getAllGradesForStation(Long competitionId, Long gradingGroupId, Long stationId);
}
