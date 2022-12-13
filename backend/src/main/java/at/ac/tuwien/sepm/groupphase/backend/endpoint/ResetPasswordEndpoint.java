package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.h2.engine.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/reset")
public class ResetPasswordEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;

    public ResetPasswordEndpoint(UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @PostMapping
    public String resetPassword(@RequestBody UserPasswordResetDto userPasswordResetDto) {
        String token = userPasswordResetDto.getToken();
        String password = userPasswordResetDto.getPassword();
        Optional<SecurityUser> account = userService.getSecurityUserByResetToken(token);

        if (account.isPresent()) {
            SecurityUser temp = account.get();
            userService.updateSecurityUserPassword(temp, password);
            return "{\"success\": \"true\"}";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid token for password reset");
        }
    }
}
