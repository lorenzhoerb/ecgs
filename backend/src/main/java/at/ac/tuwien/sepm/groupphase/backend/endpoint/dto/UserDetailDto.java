package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;

import java.util.Date;

public record UserDetailDto(
    Long id,
    String firstName,
    String lastName,
    ApplicationUser.Gender gender,
    Date dateOfBirth,
    String picturePath
) {
}
