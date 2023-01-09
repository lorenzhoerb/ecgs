package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;

public record CalenderViewCompetitionDto(
    Integer id,
    String name,
    LocalDateTime beginOfCompetition,
    LocalDateTime endOfCompetition,
    String description,
    String picturePath,
    Boolean isPublic
) {

}
