package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;

public record CompetitionDto(
    Long id,
    String name,
    LocalDateTime beginOfRegistration,
    LocalDateTime endOfRegistration,
    LocalDateTime beginOfCompetition,
    LocalDateTime endOfCompetition,
    String description,
    String picturePath,
    Boolean isPublic,
    Boolean draft,
    String email,
    String phone
) {

}
