package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class ExcelReportDownloadResponseDto {
    private String name;

    public ExcelReportDownloadResponseDto(String name) {
        this.name = name;
    }

    public ExcelReportDownloadResponseDto() {
    }

    public String getName() {
        return this.name;
    }
}
