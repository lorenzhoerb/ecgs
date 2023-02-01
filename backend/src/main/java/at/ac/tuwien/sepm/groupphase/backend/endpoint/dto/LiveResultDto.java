package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;


public record LiveResultDto(
    List<GradeResultDto> grades
) {
    public LiveResultDto withGrades(List<GradeResultDto> grades) {
        return new LiveResultDto(grades);
    }
}
