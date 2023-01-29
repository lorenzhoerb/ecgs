package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportDownloadResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ReportFile;
import org.mapstruct.Mapper;

@Mapper
public interface ReportFileMapper {
    ExcelReportDownloadResponseDto reportFileToExcelReportDownloadResponseDto(ReportFile reportFile);
}
