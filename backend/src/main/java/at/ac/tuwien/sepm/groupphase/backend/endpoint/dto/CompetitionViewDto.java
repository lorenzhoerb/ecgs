package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;

public record CompetitionViewDto(
    String name,
    LocalDateTime beginOfRegistration,
    LocalDateTime endOfRegistration,
    LocalDateTime endOfCompetition,
    LocalDateTime beginOfCompetition,
    String description,
    String picturePath,
    Boolean isPublic,
    Boolean draft,
    String email,
    String phone
) {
}
