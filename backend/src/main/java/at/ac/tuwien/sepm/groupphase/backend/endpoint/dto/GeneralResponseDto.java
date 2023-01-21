package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.helptypes.StatusText;

public record GeneralResponseDto(
    String message
) {
    public GeneralResponseDto(String message) {
        this.message = message;
    }
}

