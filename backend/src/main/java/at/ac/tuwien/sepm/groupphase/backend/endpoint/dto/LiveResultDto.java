package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.With;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;


@With
public record LiveResultDto(
    List<GradeResultDto> grades
) {
}
