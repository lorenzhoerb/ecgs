package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;

// @TODO: add validation here with a annotations
// Don't need to -> its output-data-structure
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
