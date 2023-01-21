package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.With;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;


@With
public record GradeDto(
    @NotNull(message = "must have a uuid attached to the message")
    UUID uuid,

    @NotNull(message = "judgeId can't be empty")
    Long judgeId,

    @NotNull(message = "participantId must be given")
    Long participantId,

    @NotNull(message = "competitionId can't be empty")
    Long competitionId,

    @NotNull(message = "gradingGroupId must be given")
    Long gradingGroupId,

    @NotNull(message = "stationId must be given")
    Long stationId,

    @Size(max = 4096, message = "grade to long")
    @NotNull(message = "grade must be given")
    String grade
) {
}
