package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;

import java.util.Date;
import java.util.List;

public record UserDetailGradeDto(
    Long id,
    String firstName,
    String lastName,
    ApplicationUser.Gender gender,
    Date dateOfBirth,
    String picturePath,
    List<StationResultDto> grades,
    Double finalResult
) {
}
