package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/registration")
public class RegistrationEndpoint {
    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public RegistrationEndpoint(UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "registers a new user")
    public String register(@RequestBody UserRegisterDto userRegisterDto) {
        LOGGER.info("POST {}", "/api/v1/registration");
        ApplicationUser applicationUser = userService.registerUser(userRegisterDto);
        return "Account with email: " + applicationUser.getUser().getEmail() + " created!";
    }
}
