package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public record ClubManagerTeamImportDto(
    @NotBlank(message = "Team name is blank") // basically min = 1
    @Size(max = 255, message = "Team name is too long")
    @Pattern(regexp = "^[a-zA-Z \\u00C0-\\u017F0-9/_-]*$",
        message = "Team name can only include letters, digits, spaces and special characters like - / _")
    String teamName,
    List<ClubManagerTeamMemberImportDto> teamMembers
) {
}
