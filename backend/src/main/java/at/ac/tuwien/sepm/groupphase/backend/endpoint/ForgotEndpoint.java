package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/forgot")
public class ForgotEndpoint {

    static final String BASE_PATH = "/api/v1/forgot";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;



    @Autowired
    public ForgotEndpoint(UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @CrossOrigin
    @PostMapping
    @Operation(summary = "request a change of password")
    public String requestResetPassword(@RequestBody UserPasswordResetRequestDto userPasswordResetRequestDto) {
        LOGGER.info("POST {}", BASE_PATH);
        return userService.prepareAndSendPasswordResetMail(userPasswordResetRequestDto);
    }

}
