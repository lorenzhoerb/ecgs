package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
public class ReportDownloadInclusionRuleOptionsDto {
    boolean canGenerateReportForTeam;
    boolean canGenerateReportForSelf;

    public ReportDownloadInclusionRuleOptionsDto() {
    }

    public ReportDownloadInclusionRuleOptionsDto(Boolean canGenerateReportForTeam, Boolean canGenerateReportForSelf) {
        this.canGenerateReportForTeam = canGenerateReportForTeam;
        this.canGenerateReportForSelf = canGenerateReportForSelf;
    }

    public boolean getCanGenerateReportForTeam() {
        return canGenerateReportForTeam;
    }

    public boolean getCanGenerateReportForSelf() {
        return canGenerateReportForSelf;
    }
}
