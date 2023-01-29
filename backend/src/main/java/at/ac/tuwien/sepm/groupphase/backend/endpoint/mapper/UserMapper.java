package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamMemberImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailSetFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailGradeDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper {

    @Mapping(source = "email", target = "user.email")
    @Mapping(source = "password", target = "user.password")
    ApplicationUser registerDtoToApplicationUser(UserRegisterDto userRegisterDto);

    UserRegisterDto clubManagerTeamMemberImportToUserRegisterDto(ClubManagerTeamMemberImportDto teamMemberImportDto);

    UserDetailDto applicationUserToUserDetailDto(ApplicationUser applicationUser);

    UserDetailFlagDto applicationUserToUserDetailFlagDto(ApplicationUser applicationUser);

    Set<UserDetailDto> applicationUserSetToUserDetailDtoSet(Set<ApplicationUser> users);

    List<UserDetailDto> applicationUserListToUserDetailDtoList(List<ApplicationUser> users);

    List<UserDetailFlagDto> applicationUserListToUserDetailFlagDtoList(List<ApplicationUser> users);

    List<ApplicationUser> userDetailSetFlagDtoListToApplicationUserList(List<UserDetailSetFlagDto> users);

    Set<ApplicationUser> userDetailDtoSetToApplicationUserSet(Set<UserDetailDto> users);

    List<UserDetailGradeDto> userDetailDtoToUserDetailGradeDto(List<UserDetailDto> users);
}
