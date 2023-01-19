package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupWithRegisterToDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper
public interface GradeMapper {
    @Mapping(source = "gradePk.judgeId", target = "judgeId")
    @Mapping(source = "gradePk.participantId", target = "participantId")
    @Mapping(source = "gradePk.competitionId", target = "competitionId")
    @Mapping(source = "gradePk.gradingGroupId", target = "gradingGroupId")
    @Mapping(source = "gradePk.stationId", target = "stationId")
    @Mapping(source = "grading", target = "grade")
    GradeDto gradeToGradeDto(Grade gradingGroup);

    @Mapping(source = "judgeId", target = "gradePk.judgeId")
    @Mapping(source = "participantId", target = "gradePk.participantId")
    @Mapping(source = "competitionId", target = "gradePk.competitionId")
    @Mapping(source = "gradingGroupId", target = "gradePk.gradingGroupId")
    @Mapping(source = "stationId", target = "gradePk.stationId")
    @Mapping(source = "grade", target = "grading")
    Grade gradeDtoToGrade(GradeDto gradingGroupDetailDto);

    GradeResultDto gradeDtoToGradeResultDto(GradeDto gradeDto);

    @Mapping(source = "gradePk.judgeId", target = "judgeId")
    @Mapping(source = "gradePk.participantId", target = "participantId")
    @Mapping(source = "gradePk.competitionId", target = "competitionId")
    @Mapping(source = "gradePk.gradingGroupId", target = "gradingGroupId")
    @Mapping(source = "gradePk.stationId", target = "stationId")
    @Mapping(source = "grading", target = "grade")
    @Mapping(source = "valid", target = "isValid")
    GradeResultDto gradeToGradeResultDto(Grade grade);

}
