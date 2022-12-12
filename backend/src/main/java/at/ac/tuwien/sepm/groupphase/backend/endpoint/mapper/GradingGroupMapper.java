package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GradingGroupMapper {
    GradingGroupDto gradingGroupToGradingGroupDetailDto(GradingGroup gradingGroup);

    GradingGroup gradingGroupDetailDtoToGradingGroup(GradingGroupDto gradingGroupDetailDto);
}
