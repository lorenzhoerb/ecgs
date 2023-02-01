package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportDownloadResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportGenerationRequestDto;


public interface ReportFileService {
    /**
     * Returns the name the given report can be downloaded by.
     *
     * @param requestDto the report requested from the client
     * @return the dto holding the name the file can be downloaded by
     */
    ExcelReportDownloadResponseDto downloadExcelReport(ExcelReportGenerationRequestDto requestDto);
}
