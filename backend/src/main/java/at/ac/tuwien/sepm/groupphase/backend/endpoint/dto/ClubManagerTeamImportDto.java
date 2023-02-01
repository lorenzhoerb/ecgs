package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public record ClubManagerTeamImportDto(
    List<ClubManagerTeamMemberImportDto> teamMembers
) {
}
