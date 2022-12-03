package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;

public record ErrorListRestDto(
    String message,
    List<String> errors
) {
}
