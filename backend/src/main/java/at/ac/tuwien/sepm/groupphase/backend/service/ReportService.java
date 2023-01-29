package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportDownloadResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportGenerationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PageableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantCompetitionResultDto;
import at.ac.tuwien.sepm.groupphase.backend.report.Report;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReportService {

    void calculateResultsOfCompetition(Long competitionId);

    List<ParticipantCompetitionResultDto> getParticipantResults();

    Report generateFilteredReport(ExcelReportGenerationRequestDto requestDto);
}
