package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.enums.ExcelReportGenerationRequestInclusionRule;

import javax.validation.constraints.NotNull;
import java.util.Set;

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

    public @NotNull(message = "Competition id must be specified") Long getCompetitionId() {
        return this.competitionId;
    }

    public @NotNull(message = "Grading groups must be specified") Set<Long> getGradingGroupsIds() {
        return this.gradingGroupsIds;
    }

    public @NotNull(message = "Participants to include must be specified") ExcelReportGenerationRequestInclusionRule getInclusionRule() {
        return this.inclusionRule;
    }

    public ExcelReportGenerationRequestDto withCompetitionId(@NotNull(message = "Competition id must be specified") Long competitionId) {
        return this.competitionId == competitionId ? this : new ExcelReportGenerationRequestDto(competitionId, this.gradingGroupsIds, this.inclusionRule);
    }

    public ExcelReportGenerationRequestDto withGradingGroupsIds(@NotNull(message = "Grading groups must be specified") Set<Long> gradingGroupsIds) {
        return this.gradingGroupsIds == gradingGroupsIds ? this : new ExcelReportGenerationRequestDto(this.competitionId, gradingGroupsIds, this.inclusionRule);
    }

    public ExcelReportGenerationRequestDto withInclusionRule(
        @NotNull(message = "Participants to include must be specified") ExcelReportGenerationRequestInclusionRule inclusionRule) {
        return this.inclusionRule == inclusionRule ? this : new ExcelReportGenerationRequestDto(this.competitionId, this.gradingGroupsIds, inclusionRule);
    }

    public void setCompetitionId(@NotNull(message = "Competition id must be specified") Long competitionId) {
        this.competitionId = competitionId;
    }

    public void setGradingGroupsIds(@NotNull(message = "Grading groups must be specified") Set<Long> gradingGroupsIds) {
        this.gradingGroupsIds = gradingGroupsIds;
    }

    public void setInclusionRule(@NotNull(message = "Participants to include must be specified") ExcelReportGenerationRequestInclusionRule inclusionRule) {
        this.inclusionRule = inclusionRule;
    }
}
