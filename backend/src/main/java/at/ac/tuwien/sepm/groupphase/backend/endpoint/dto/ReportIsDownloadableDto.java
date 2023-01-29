package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportIsDownloadableDto {
    boolean downloadable;

    public ReportIsDownloadableDto() {
    }

    public ReportIsDownloadableDto(Boolean downloadable) {
        this.downloadable = downloadable;
    }
}
