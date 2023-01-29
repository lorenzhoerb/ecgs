package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportDownloadResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportGenerationRequestDto;


public interface ReportFileService {
    ExcelReportDownloadResponseDto downloadExcelReport(ExcelReportGenerationRequestDto requestDto);
}
