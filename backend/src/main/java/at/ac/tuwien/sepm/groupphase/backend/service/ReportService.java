package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportDownloadResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportGenerationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PageableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantCompetitionResultDto;
import at.ac.tuwien.sepm.groupphase.backend.report.Report;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReportService {

    /**
     * Calculate all the results from a competition and create a report.
     *
     * @param competitionId the id to calculate results for
     */
    void calculateResultsOfCompetition(Long competitionId);

    /**
     * Get the results for a single participant.
     *
     * @return the resulting grades in a dto
     */
    List<ParticipantCompetitionResultDto> getParticipantResults();

    /**
     * Creates a reported filtered by the conditions specified in the requestDto.
     *
     * @param requestDto the filter conditions
     * @return the report filtered by the given conditions
     */
    Report generateFilteredReport(ExcelReportGenerationRequestDto requestDto);
}
