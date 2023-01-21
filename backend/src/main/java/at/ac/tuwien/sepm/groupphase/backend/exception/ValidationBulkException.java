package at.ac.tuwien.sepm.groupphase.backend.exception;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BulkErrorDto;

import java.util.List;

public class ValidationBulkException extends RuntimeException {
    private String messageSummary;
    private List<BulkErrorDto> bulkErrors;

    public ValidationBulkException(String messageSummary, List<BulkErrorDto> bulkErrors) {
        this.messageSummary = messageSummary;
        this.bulkErrors = bulkErrors;
    }

    public String getMessageSummary() {
        return messageSummary;
    }

    public List<BulkErrorDto> getBulkErrors() {
        return bulkErrors;
    }
}
