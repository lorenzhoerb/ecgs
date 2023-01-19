package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class UserDetailSetFlagDto {
    @NotNull(message = "flag must be set")
    @NotBlank(message = "flag must not be blank")
    private SimpleFlagDto flag;
    @NotNull(message = "users must be set")
    @NotBlank(message = "users must not be blank")
    private Set<UserDetailDto> users;

    public SimpleFlagDto getFlag() {
        return flag;
    }

    public void setFlag(SimpleFlagDto flag) {
        this.flag = flag;
    }

    public Set<UserDetailDto> getUsers() {
        return users;
    }

    public void setUsers(Set<UserDetailDto> users) {
        this.users = users;
    }
}
