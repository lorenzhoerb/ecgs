package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper
public interface UserMapper {

    @Mapping(source = "email", target = "user.email")
    @Mapping(source = "password", target = "user.password")
    ApplicationUser registerDtoToApplicationUser(UserRegisterDto userRegisterDto);

    UserDetailDto applicationUserToUserDetailDto(ApplicationUser applicationUser);

    Set<UserDetailDto> applicationUserSetToUserDetailDtoSet(Set<ApplicationUser> users);
}
