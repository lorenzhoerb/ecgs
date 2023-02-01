package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class ReportIsDownloadableDto {
    boolean downloadable;

    public ReportIsDownloadableDto() {
    }

    public ReportIsDownloadableDto(Boolean downloadable) {
        this.downloadable = downloadable;
    }

    public boolean isDownloadable() {
        return this.downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }
}
