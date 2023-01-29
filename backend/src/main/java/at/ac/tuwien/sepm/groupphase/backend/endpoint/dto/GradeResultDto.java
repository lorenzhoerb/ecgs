package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Grade;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Station;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.With;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;


@With
public record GradeResultDto(
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
    String grade,

    @NotNull(message = "validity must be given")
    Boolean isValid,

    Double result,

    @JsonIgnore
    Station savedStationThatCalculatedResult
) {
}
