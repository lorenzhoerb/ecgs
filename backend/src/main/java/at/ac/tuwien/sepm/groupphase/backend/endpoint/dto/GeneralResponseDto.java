package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.helptypes.StatusText;

import java.util.List;

public record GeneralResponseDto(
    StatusText status,
    String message
) {
    public GeneralResponseDto(StatusText status, String message) {
        this.status = status;
        this.message = message;
    }
}

