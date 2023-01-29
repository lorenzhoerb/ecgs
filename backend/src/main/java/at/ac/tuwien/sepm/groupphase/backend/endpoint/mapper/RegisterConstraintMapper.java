package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BasicRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RegisterConstraintMapper {

    @Mapping(source = "value", target = "constraintValue")
    RegisterConstraint basicRegisterConstraintDtoToRegisterConstraint(BasicRegisterConstraintDto registerConstraintDto);

    @Mapping(source = "constraintValue", target = "value")
    DetailedRegisterConstraintDto registerConstraintToDetailedRegisterConstraintDto(RegisterConstraint registerConstraint);
}
