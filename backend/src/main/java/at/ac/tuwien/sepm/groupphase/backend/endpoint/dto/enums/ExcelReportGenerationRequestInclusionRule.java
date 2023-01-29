package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.enums;

public enum ExcelReportGenerationRequestInclusionRule {
    ONLY_YOU(0), ONLY_YOUR_TEAM(1), ALL_PARTICIPANTS(2);

    private final Integer value;

    ExcelReportGenerationRequestInclusionRule(Integer ruleValue) {
        value = ruleValue;
    }

    public Integer getValue() {
        return this.value;
    }
}
