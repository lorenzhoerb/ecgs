package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.helptypes.StatusText;

public record GeneralResponseDto(
    StatusText status,
    String message
) {
    public GeneralResponseDto(StatusText status, String message) {
        this.status = status;
        this.message = message;
    }
}

