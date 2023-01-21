package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;

public record BulkErrorListDto(
    String message,
    List<BulkErrorDto> errors
) {
}
