package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

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

    public void setCanGenerateReportForTeam(boolean canGenerateReportForTeam) {
        this.canGenerateReportForTeam = canGenerateReportForTeam;
    }

    public void setCanGenerateReportForSelf(boolean canGenerateReportForSelf) {
        this.canGenerateReportForSelf = canGenerateReportForSelf;
    }
}
