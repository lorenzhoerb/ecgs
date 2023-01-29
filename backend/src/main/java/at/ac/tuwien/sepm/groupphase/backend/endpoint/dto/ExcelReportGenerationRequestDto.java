package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.enums.ExcelReportGenerationRequestInclusionRule;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@With
public class ExcelReportGenerationRequestDto {
    @NotNull(message = "Competition id must be specified")
    private Long competitionId;

    @NotNull(message = "Grading groups must be specified")
    private Set<Long> gradingGroupsIds;

    @NotNull(message = "Participants to include must be specified")
    private ExcelReportGenerationRequestInclusionRule inclusionRule;

    public ExcelReportGenerationRequestDto(Long competitionId, Set<Long> gradingGroupsIds, ExcelReportGenerationRequestInclusionRule inclusionRule) {
        this.competitionId = competitionId;
        this.gradingGroupsIds = gradingGroupsIds;
        this.inclusionRule = inclusionRule;
    }

    public ExcelReportGenerationRequestDto() {
    }
}
