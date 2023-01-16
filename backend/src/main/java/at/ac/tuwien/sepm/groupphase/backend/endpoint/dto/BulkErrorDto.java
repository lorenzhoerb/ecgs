package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;

public record BulkErrorDto(
    Long id,
    List<String> errors
) {
}
