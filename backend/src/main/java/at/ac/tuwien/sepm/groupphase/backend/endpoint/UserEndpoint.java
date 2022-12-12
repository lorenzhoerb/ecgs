package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserEndpoint {

    private final UserService userService;
    private final UserMapper userMapper;

    private final SessionUtils sessionUtils;

    @Autowired
    public UserEndpoint(UserService userService, UserMapper userMapper, SessionUtils sessionUtils) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.sessionUtils = sessionUtils;
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_TOURNAMENT_MANAGER", "ROLE_CLUB_MANAGER"})
    @GetMapping
    public UserInfoDto getUser() {
        ApplicationUser user = sessionUtils.getSessionUser();
        return UserInfoDto.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getType())
            .build();
    }

}
