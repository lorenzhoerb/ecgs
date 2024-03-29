package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupWithRegisterToDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface GradingGroupMapper {
    GradingGroupDto gradingGroupToGradingGroupDetailDto(GradingGroup gradingGroup);

    GradingGroup gradingGroupDetailDtoToGradingGroup(GradingGroupDto gradingGroupDetailDto);

    List<GradingGroupWithRegisterToDto> gradingGroupToGradingGroupRegistrationDto(List<GradingGroup> gradingGroupSet);

}
