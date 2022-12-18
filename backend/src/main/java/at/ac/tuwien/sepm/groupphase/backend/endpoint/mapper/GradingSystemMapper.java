package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GradingSystemMapper {
    @Mapping(source = "isPublic", target = "public")
    GradingSystem gradingSystemDetailDtoToGradingSystem(GradingSystemDetailDto gradingSystemDetailDto);

    @Mapping(source = "public", target = "isPublic")
    GradingSystemDetailDto gradingSystemToGradingSystemDetailDto(GradingSystem gradingSystem);
}
