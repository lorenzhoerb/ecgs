package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCredentialUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/changePassword")
public class ChangePasswordEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;

    public ChangePasswordEndpoint(UserService userService) {
        this.userService = userService;
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @PostMapping
    @Operation(summary = "Change password for logged in user", security = @SecurityRequirement(name = "apiKey"))
    public String changePassword(@RequestBody UserCredentialUpdateDto userCredentialUpdateDto) {
        String email = userCredentialUpdateDto.getEmail();
        String password = userCredentialUpdateDto.getPassword();
        LOGGER.info(email);
        LOGGER.info(password);
        Optional<SecurityUser> account = userService.findSecurityUserByEmail(email);

        if (account.isPresent()) {
            SecurityUser temp = account.get();
            userService.updateSecurityUserPassword(temp, password);
            return "{\"success\": \"true\"}";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid email for password change");
        }
    }
}
